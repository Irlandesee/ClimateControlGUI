import requests

url = "https://api.random.org/json-rpc/4/invoke"
api_key = "b1579b15-ecb5-47c3-bcb8-9548ee05f230"
test_packet = {
    "jsonrpc": "2.0",
    "method": "generateStrings",
    "params": {
        "apiKey": api_key,
        "n": 8,
        "length": 10,
        "characters": "abcdefghijklmnopqrstuvwxyz",
        "replacement": True
    },
    "id": 42
}
r = requests.post(url, json=test_packet)
print(r.status_code)
print(r.json())
