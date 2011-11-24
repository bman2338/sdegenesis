// INSERT INTO Developers (name) VALUES ('dev1');
String addDeveloper(String dname) {
	"INSERT INTO Developers (name) VALUES ("+dname+");"
}

// INSERT INTO Revisions (comment, revision_number, developer_id, revision_date) VALUES ('This is a useless commit comment', 1, 1, GETDATE());
String addRevision(String comment, int revNum, String devName, String commitDate) {
	int devId = getDevId(devName);
	"INSERT INTO Revisions (comment, revision_number, developer_id, revision_date) 
				VALUES ("+comment+", "+revNum+", "+devId+", "+commitDate+");"
}

// SELECT id FROM Developers WHERE name = 'dev1'
int getDevId(String devName) {
	"SELECT id FROM Developers WHERE name = "+devName;
}

// INSERT INTO Projects (comment, revision_number, developer_id, revision_date) VALUES ('This is a useless commit comment', 1, 1, GETDATE());
String addProject(String name, String source_language, String source_dialect, int revNum) {
	int devId = getDevId(devName);
	"INSERT INTO Revisions (comment, revision_number, developer_id, revision_date) 
				VALUES ("+comment+", "+revNum+", "+devId+", "+commitDate+");"
}