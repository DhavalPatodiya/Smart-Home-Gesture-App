# -*- coding: utf-8 -*-


import os
from flask import Flask, flash, request, redirect, url_for
from werkzeug.utils import secure_filename
  
# Flask Constructor
app = Flask(__name__)

@app.route("/")
def showHomePage():
    return "This is home page"

@app.route('/upload', methods=["POST"])
def upload():
    if 'file' in request.files:
            return redirect(request.url)
    if 'video' in request.files:
        video = request.files['video']
        filename = secure_filename(video.filename)
        # Secure the filename to prevent some kinds of attack
        if filename != '':
            file_name = os.path.splitext(filename)[0]
            file_ext = os.path.splitext(filename)[1]
            i = 1
            while os.path.exists(os.path.join("/Users/dhaval/Desktop/Gestures",file_name+"_PRACTICE_"+str(i)+"_PATODIYA"+file_ext)):
                i += 1
            video.save(os.path.join("/Users/dhaval/Desktop/Gestures",file_name+"_PRACTICE_"+str(i)+"_PATODIYA"+file_ext))
            return "Video is Saved"
    
    return "Some Error Occurred"
  
if __name__ == "__main__":
  app.run(host="0.0.0.0")
