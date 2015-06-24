# TruecallerDropwizard : 
Take home test from Truecaller based on dropwizard framework.Components used: Jetty, Jackson, Jersey, Hibernate-Validator, H2-File-based-SqlDb, Hibernate.

Instructions:
=============
1) Go to truecaller-hw folder.
2) run mvn package
3) run java -jar .\target\truecaller-hw-1.0.jar server example.yml
4) Start making REST calls now.

REST apis:
==========
1) GET : http://localhost:1334/user/hello
-> to check if /user endpoint is working

2) POST : http://localhost:1334/user/create/
-> create a user object inside Db
no params needed
will return the created object

3) POST : http://localhost:1334/user/add_view/1
-> to add a profile-view in user-object; 1 is the userId(viewee)
params:
{
  "id":2    (id of the viewer)
}
return the user's updated object

4) GET : http://localhost:1334/user/viewers/1
-> to view profile-views of a user
return array of viewers


Database Design:
================
Person Object:
Fields:
- id => long => unique id of a user/person; auto-generated 
- viewers => string => viewer object for this user, stored in form of json

Viewer Object:
Fields:
- id => long => unique of the viewer, who is viewing someone's profile
- timestamp => long => server's timestamp of the view
- dateTime => String => date & time of the view
