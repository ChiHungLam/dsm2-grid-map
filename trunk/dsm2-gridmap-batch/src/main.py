#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# google 
from google.appengine.api import users
from google.appengine.ext import blobstore
from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.ext.webapp import util
from google.appengine.ext.webapp import blobstore_handlers
# 3rd party
import base64
# local
from models import DEMDataFile, BathymetryDataFile
import ops

class MainHandler(webapp.RequestHandler):
    def get(self):
        user = users.get_current_user()
        if not user:
            self.redirect(users.create_login_url(self.request.uri))
        else:
            self.redirect("/upload_dem_csv")
class DisplayUploadDEMZipHandler(webapp.RequestHandler):
    def get(self):
        upload_url = blobstore.create_upload_url("/upload_dem_blob")
        self.response.out.write('<html><body>')
        self.response.out.write("""<p>Upload a file of line(s) of DEMDataFile/BathymetryDataFile in csv format.</p>
        <p>The columns expected are y,x and contents and the first line is ignored</p>""")
        self.response.out.write('<form action="%s" method="POST" enctype="multipart/form-data">' % upload_url)
        self.response.out.write("""Upload File: <input type="file" name="file"><br> <input type="submit" 
        name="submit" value="Submit"> </form></body></html>""")
class DEMZipUploadHandler(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        upload_files = self.get_uploads('file') # 'file' is the file upload field in the format
        blob_info = upload_files[0]
        self.redirect("/mapreduce")
        #self.response.out.write('A zipped file (blob_key: %s) is read for importing into datastore'%blob_info.key());
class ConvertBathyHandler(webapp.RequestHandler):
    def get(self):
        count = self.request.get('count')
        if not count:
            count = 100
        else:
            count = int(count)
        ops.do_all_copy_and_delete_bathy(count)
class DeleteBathyWithNamedId(webapp.RequestHandler):
    def get(self):
        sid = int(self.request.get('start_id'))
        ops.do_delete_with_named_id_bathy(sid,200)
class ConvertDEMHandler(webapp.RequestHandler):
    def get(self):
        count = self.request.get('count')
        if not count:
            count = 100
        else:
            count = int(count)
        ops.do_all_copy_and_delete_dem(count)
class DeleteDEMWithNamedId(webapp.RequestHandler):
    def get(self):
        sid = int(self.request.get('start_id'))
        ops.do_delete_with_named_id_dem(sid,200)
def update_insert_dem(input):
    startByte = input[0]
    if int(startByte)==0: # ignore first line..its a header
        return;
    line = input[1]
    fields=line.split(',')
    print line, fields
    x=int(fields[1])
    y=int(fields[0])
    contents=db.Blob(base64.b64decode(fields[2]))
    e=DEMDataFile.get_or_insert(fields[1]+'_'+fields[0])
    e.x=x
    e.y=y
    e.contents=contents
    e.put()
def update_insert_bathy(input):
    startByte = input[0]
    if int(startByte)==0: # ignore first line..its a header
        return;
    line = input[1]
    fields=line.split(',')
    print line, fields
    x=int(fields[1])
    y=int(fields[0])
    contents=db.Blob(base64.b64decode(fields[2]))
    e=BathymetryDataFile.get_or_insert(fields[1]+'_'+fields[0])
    e.x=x
    e.y=y
    e.contents=contents
    e.put()

def main():
    application = webapp.WSGIApplication([('/upload_dem_csv',DisplayUploadDEMZipHandler),
                                          ('/upload_dem_blob',DEMZipUploadHandler),
                                          ('/convert_bathy',ConvertBathyHandler),
                                          ('/delete_bathy_id',DeleteBathyWithNamedId),
                                          ('/convert_dem',ConvertDEMHandler),
                                          ('/delete_dem_id',DeleteDEMWithNamedId),
                                          ('/.*', MainHandler)],
                                         debug=True)
    util.run_wsgi_app(application)


if __name__ == '__main__':
    main()
