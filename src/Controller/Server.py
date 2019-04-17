from flask import Flask, send_from_directory
from flask_socketio import SocketIO
app = Flask(__name__)
socket_server = SocketIO(app)

@app.route('/')
def index():
    return send_from_directory('frontend', 'web.html')


usernameToSid = {}
@socket_server.on('register')
def got_message(username):
    usernameToSid[username] = request.sid

# Possibly make each response individual for each button(ex. W,A,S,D, MouseClick) or all one response
@socket_server.on('projectile')
def got_message(jason):
    usernameToSid[username] = request.sid


@socket_server.on('Movement')
def got_message(jason):
    usernameToSid[username] = request.sid

socket_server.run(app, port=8080)