from flask import Flask, send_from_directory
from flask_socketio import SocketIO
import eventlet
import socket
import json
from threading import Thread

eventlet.monkey_patch()
app = Flask(__name__)
socket_server = SocketIO(app)
# model_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# model_socket.connect(('localhost', 8000)) #only if main is active

"""
def listen_to_model(the_socket):
    delimiter = "~"
    buffer = ""
    while True:
        buffer += the_socket.recv(1024).decode()
        while delimiter in buffer:
            message = buffer[:buffer.find(delimiter)]
            buffer = buffer[buffer.find(delimiter)+1:]
            socket_server.emit('message', message)


Thread(target=listen_to_model, args=(model_socket,)).start()"""


@app.route('/')
def index():
    return send_from_directory('../frontend', 'web.html')


@app.route('/frontend.js')
def frontJS():
    return send_from_directory('../frontend/js', 'frontend.js')


@app.route("/assets/<path:path>")
def asset(path):
    return send_from_directory('../frontend/assets', path)


usernameToSid = {}
sidToUsername = {}


@socket_server.on('register')
def got_message(username, jason):
    usernameToSid[username] = request.sid
    sidToUsername[request.sid] = username
    delimiter = "~"
    data = {"action": "regular", "data": json.loads(jason)}
    model_socket.sendall((json.dumps(data) + delimiter).encode())


@socket_server.on('disconnect')
def got_connection():
    if request.sid in sidToUsername:
        username = sidToUsername[request.sid]
        del sidToUsername[request.sid]
        del usernameToSid[username]
        delimiter = "~"
        data = {"username": username, "action": "disconnect"}
        model_socket.sendall((json.dumps(data) + delimiter).encode())



# Possibly make each response individual for each button(ex. W,A,S,D, MouseClick) or all one response


""""@socket_server.on('projectile')
def got_message(jason):
    model_socket.sendall(json.dumps(jason).encode())"""


@socket_server.on('Jason')
def got_message(jason):
    print("message")
    data = {"action": "regular", "data": json.loads(jason)}
    delimiter = "~"
    model_socket.sendall((json.dumps(data) + delimiter).encode())


app_port = 8069
print("server at localhost:" + str(app_port))
socket_server.run(app, port=app_port)
