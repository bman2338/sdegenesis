CREATE DATABASE Genesis_db;

CREATE TABLE Developers
(
id int NOT NULL AUTO_INCREMENT,
name varchar(255) NOT NULL,
PRIMARY KEY (id),
UNIQUE (name)
);

CREATE TABLE Methods
(
id int NOT NULL AUTO_INCREMENT,
name varchar(255) NOT NULL,
signature varchar(255) NOT NULL,
modifiers varchar(255) NOT NULL,
return_type varchar(255) NOT NULL,
owner int NOT NULL,
revision_number int NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (owner) REFERENCES Developers(id)
);

CREATE TABLE Attributes
(
id int NOT NULL AUTO_INCREMENT,
name varchar(255) NOT NULL,
signature varchar(255) NOT NULL,
modifiers varchar(255) NOT NULL,
declared_type varchar(255) NOT NULL,
revision_number int NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE Classes
(
id int NOT NULL AUTO_INCREMENT,
name varchar(255) NOT NULL,
revision_number int NOT NULL,
owner int NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (owner) REFERENCES Developers(id)
);

CREATE TABLE ClassToAttributes
(
class_id int NOT NULL,
attribute_id int NOT NULL,
PRIMARY KEY (class_id, attribute_id),
FOREIGN KEY (class_id) REFERENCES Classes(id),
FOREIGN KEY (attribute_id) REFERENCES Attributes(id)
);

CREATE TABLE ClassToMethods
(
class_id int NOT NULL,
method_id int NOT NULL,
PRIMARY KEY (class_id, method_id),
FOREIGN KEY (class_id) REFERENCES Classes(id),
FOREIGN KEY (method_id) REFERENCES Methods(id)
);

CREATE TABLE Packages
(
id int NOT NULL AUTO_INCREMENT,
name varchar(255) NOT NULL,
owner int NOT NULL,
revision_number int NOT NULL,
belongs_to_package int,
PRIMARY KEY (id),
FOREIGN KEY (owner) REFERENCES Developers(id),
FOREIGN KEY (belongs_to_package) REFERENCES Packages(id)
);

CREATE TABLE PackageToClasses
(
package_id int NOT NULL,
class_id int NOT NULL,
PRIMARY KEY (package_id, class_id),
FOREIGN KEY (package_id) REFERENCES Packages(id),
FOREIGN KEY (class_id) REFERENCES Classes(id)
);

CREATE TABLE Projects
(
id int NOT NULL AUTO_INCREMENT,
name varchar(255) NOT NULL,
source_language varchar(255) NOT NULL,
source_dialect varchar(255),
PRIMARY KEY (id)
);

CREATE TABLE ProjectToPackages
(
project_id int NOT NULL,
package_id int NOT NULL,
PRIMARY KEY (project_id, package_id),
FOREIGN KEY (project_id) REFERENCES Projects(id),
FOREIGN KEY (package_id) REFERENCES Packages(id)
);

CREATE TABLE Revisions
(
project_id int NOT NULL AUTO_INCREMENT,
comment varchar(255) NOT NULL,
revision_number int NOT NULL,
developer_id int NOT NULL,
revision_date datetime,
PRIMARY KEY (project_id, comment, revision_number, developer_id, revision_date),
FOREIGN KEY (project_id) REFERENCES Projects(id),
FOREIGN KEY (developer_id) REFERENCES Developers(id)
);

CREATE TABLE Inheritace
(
subclass int NOT NULL,
superclass int NOT NULL,
revision_number int NOT NULL,
PRIMARY KEY (subclass, superclass, revision_number),
FOREIGN KEY (subclass) REFERENCES Classes(id),
FOREIGN KEY (superclass) REFERENCES Classes(id)
);

CREATE TABLE Candidates
(
id int NOT NULL,
candidate int NOT NULL,
revision_number int NOT NULL,
PRIMARY KEY (id, candidate, revision_number),
FOREIGN KEY (candidate) REFERENCES Methods(id)
);

CREATE TABLE Invocations
(
invoked_by int NOT NULL,
candidates int NOT NULL,
revision_number int NOT NULL,
PRIMARY KEY (invoked_by, candidates, revision_number),
FOREIGN KEY (invoked_by) REFERENCES Methods(id),
FOREIGN KEY (candidates) REFERENCES Candidates(id)
);

CREATE TABLE BugTrackerDevelopers
(
id int NOT NULL AUTO_INCREMENT,
name varchar(255) NOT NULL,
email varchar(255),
PRIMARY KEY (id),
UNIQUE (name)
);

CREATE TABLE BugTrackerHistory
(
id int NOT NULL,
previous_assignee int NOT NULL,
PRIMARY KEY (id, previous_assignee),
FOREIGN KEY (previous_assignee) REFERENCES BugTrackerDevelopers(id)
);

CREATE TABLE BugTrackerInfo
(
id int NOT NULL AUTO_INCREMENT,
name varchar(255) NOT NULL,
descriptions varchar(255) NOT NULL,
belongs_to_project int NOT NULL,
status varchar(255) NOT NULL,
assignee int NOT NULL,
bug_history int NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (belongs_to_project) REFERENCES Projects(id),
FOREIGN KEY (bug_history) REFERENCES BugTrackerHistory(id),
FOREIGN KEY (assignee) REFERENCES BugTrackerDevelopers(id)
);

CREATE TABLE MethodMetrics
(
method_id int NOT NULL,
name varchar(255) NOT NULL,
value varchar(255) NOT NULL,
revision_number int NOT NULL,
PRIMARY KEY (method_id, name, value, revision_number),
FOREIGN KEY (method_id) REFERENCES Methods(id)
);

CREATE TABLE ClassMetrics
(
class_id int NOT NULL,
name varchar(255) NOT NULL,
value varchar(255) NOT NULL,
revision_number int NOT NULL,
PRIMARY KEY (class_id, name, value, revision_number),
FOREIGN KEY (class_id) REFERENCES Classes(id)
);
