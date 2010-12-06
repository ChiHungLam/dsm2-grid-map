from google.appengine.ext import db
import time
class BathymetryDataFile(db.Model):
    x = db.IntegerProperty()
    y = db.IntegerProperty()
    contents = db.BlobProperty()
class DEMDataFile(db.Model):
    x = db.IntegerProperty()
    y = db.IntegerProperty()
    contents = db.BlobProperty()
def copy_into_xy_based_key(b):
    if b.key().name() == None:
        bn = BathymetryDataFile.get_or_insert(str(b.x) + '_' + str(b.y), x=b.x, y=b.y, contents=b.contents)
        if bn:
            b.delete()
        else:
            raise 'Could not create key based entity for ' + b.to_xml()
#
def do_key_based_for_all():
    while 1:
        try:
            earray = BathymetryDataFile.all().fetch(10)
            for e in earray:
                copy_into_xy_based_key(e)
        except:
            print 'Caught error, waiting and continuing'
            time.sleep(5)
