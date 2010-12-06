import sys; sys.path.append('.')
from google.appengine.ext import db
from models import BathymetryDataFile
from models import DEMDataFile
def delete_all_dems():
    while 1:
        print 'Deleting another 500'
        try:
            db.delete(DEMDataFile.all().fetch(500))
        except:
            print 'Caught error, continuing...'
def copy_and_delete_bathy(b):
    if b.key().name() == None:
        bn = BathymetryDataFile.get_or_insert(str(b.x)+'_'+str(b.y),x=b.x,y=b.y,contents=b.contents)
        if bn:
            b.delete()
    else:
        raise 'Could not create key based entity for '+b.to_xml()
#
def do_all_copy_and_delete_bathy(count):
    earray = BathymetryDataFile.all().fetch(count)
    if len(earray)==0:
        print 'No more to convert'
        return
    for e in earray:
        copy_and_delete_bathy(e)
    print 'Converted %d'%count
#
def do_delete_named_bathy(id):
    b=BathymetryDataFile.get_by_key_name(str(id))
    if b:
        b.delete()
#
def do_delete_with_named_id_bathy(startid, count):
    for i in range(count):
        cid = startid+i
        do_delete_named_bathy(cid)
#
def copy_and_delete_dem(b):
    if b.key().name() == None:
        bn = DEMDataFile.get_or_insert(str(b.x)+'_'+str(b.y),x=b.x,y=b.y,contents=b.contents)
        if bn:
            b.delete()
    else:
        raise 'Could not create key based entity for '+b.to_xml()
#
def do_all_copy_and_delete_dem(count):
    earray = DEMDataFile.all().fetch(count)
    if len(earray)==0:
        return
    for e in earray:
        copy_and_delete_dem(e)
#
def do_delete_named_dem(id):
    b=DEMDataFile.get_by_key_name(str(id))
    if b:
        b.delete()
#
def do_delete_with_named_id_dem(startid, count):
    for i in range(count):
        cid = startid+i
        do_delete_named_dem(cid)
#
