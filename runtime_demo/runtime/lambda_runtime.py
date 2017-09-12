import argparse
import logging
import sys
import os
import traceback

LOG = logging.getLogger(__name__)
# streamlog = logging.StreamHandler()
streamlog = logging.FileHandler('/tmp/runtime.log')
streamlog.setFormatter(logging.Formatter("%(asctime)s %(name)s %(levelname)s %(pathname)s %(filename)s.%(module)s:%(lineno)d %(message)s"))
LOG.addHandler(streamlog)
LOG.setLevel(logging.DEBUG)


def parse_args():
    parser = argparse.ArgumentParser()

    parser.add_argument('--handler', 
                        type=str, 
                        required=True, 
                        help='Handler defined for lambda function')
    args, unknown = parser.parse_known_args()
    return args

try:
    # Python 3
    from urllib.request import urlopen, Request
    from urllib.error import URLError
except ImportError:
    # Python 2
    from urllib2 import urlopen, Request, URLError

import collections
import json

HEADER_INVOCATION_ID = 'X-Invocation-Id'
HEADER_CLIENT_CONTEXT = 'X-Client-Context'

WorkItem = collections.namedtuple('WorkItem', ['invocation_id', 'payload', 'client_context'])


class IPCClient:
    def __init__(self, endpoint, port):
        self.endpoint = endpoint
        self.port = port


    def _get_url(self, func):
        return ('http://' + self.endpoint + ':' + str(self.port) +
                '/v1.0/lambda/' + func)

    def _get_result_url(self, func):
        return self._get_url(func) + '/result'

    def get(self, func):
        url = self._get_url(func)
        request = Request(url)
        response = urlopen(request)
        invocation_id = response.info().get(HEADER_INVOCATION_ID)
        client_context = response.info().get(HEADER_CLIENT_CONTEXT)
        payload = response.read()
        return WorkItem(invocation_id=invocation_id,
                payload=payload,
                client_context=client_context)

              
    def post_result(self, func, result):
        url = self._get_url(func)
        url = self._get_result_url(func)
        request = Request(url, bytes(result))
        response = urlopen(request)
        response.read()


class LambdaRuntime:
    def __init__(self, function_handler, endpoint='localhost', port=6666):
        self.ipc = IPCClient(endpoint=endpoint, port=port)
        self.function = os.getenv('MY_LAMBDA_NAME', 'testLambda')
        self.function_handler = function_handler

    def _start(self):
        try:
            # load Lambda handler
            mod_str, _sep, handler_function = self.function_handler.rpartition('.')
            __import__(mod_str)
            self.handler = getattr(sys.modules[mod_str], handler_function)
        except Exception:
            LOG.error("Failed to import %s error", self.function_handler)
            raise

        try:
            work = self.ipc.get(self.function)
        except Exception, e:
            LOG.error("Failed to get work for %s due to:%s", 
                    self.function_handler,
                    str(e))
            raise e
        if work.payload is None or len(work.payload) == 0:
            event = ''
        else:
            event = json.loads(work.payload)
        context = None
        result = self.handler(event, context)
        #proc = psutil.Process(os.getgid())
        return result #+" "+str(os.getgid())


    def start(self):
        try:
            result = self._start()
        except Exception:
            result = traceback.format_exc()
            # LOG.exception("import function %s failed!", function_handler)
        self.ipc.post_result(self.function, result)


def main():
    args = parse_args()
    LOG.debug('invoking %s', args.handler)
    runtime = LambdaRuntime(args.handler)
    runtime.start()

if __name__ == '__main__':
    main()
