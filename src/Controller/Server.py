from flask import Flask, send_from_directory
from flask_socketio import SocketIO
import eventlet
import socket
import json
from threading import Thread

eventlet.monkey_patch()
app = Flask(__name__)
socket_server = SocketIO(app)
model_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#model_socket.connect(('localhost', 8000)) only if main is active


def listen_to_model(the_socket):
    delimiter = "~"
    buffer = ""
    while True:
        buffer += the_socket.recv(1024).decode()
        while delimiter in buffer:
            message = buffer[:buffer.find(delimiter)]
            buffer = buffer[buffer.find(delimiter)+1:]
            socket_server.emit('message', message)


Thread(target=listen_to_model, args=(model_socket,)).start()


@app.route('/')
def index():
    return send_from_directory('frontend', 'web.html')


usernameToSid = {}


@socket_server.on('register')
def got_message(username):
    new_player = {"type": "New Player", "username": username}
    model_socket.sendall(json.dumps(new_player).encode())

# Possibly make each response individual for each button(ex. W,A,S,D, MouseClick) or all one response


@socket_server.on('projectile')
def got_message(jason):
    model_socket.sendall(json.dumps(jason).encode())


@socket_server.on('Movement')
def got_message(jason):
    model_socket.sendall(json.dumps(jason).encode())

socket_server.run(app, port=8080)