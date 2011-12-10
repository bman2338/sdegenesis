#export JAVA_OPTS=-Xmx1024M

#scala ../../out/production/IdeaProjects/scala/ch/usi/inf/genesis/server/Service.class
cd /Users/patrick/IdeaProjects/website
mongod --dbpath=/Users/patrick/IdeaProjects/database --port=8888  & node server.js

ps aux | grep node
ps aux | grep mongod
#ps aux | grep Service

cd /Users/patrick/IdeaProjects/scripts/shellscripts
