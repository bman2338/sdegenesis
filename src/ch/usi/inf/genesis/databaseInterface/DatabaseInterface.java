package ch.usi.inf.genesis.databaseInterface;

import java.sql.*;
import javax.sql.*;

public class DatabaseInterface{

	static String dbtime;
	static String dbUrl = "jdbc:mysql://127.0.0.1/Genesis_db";
	String dbClass = "com.mysql.jdbc.Driver";
	static String query = "INSERT INTO Developers (name) VALUES ('Dev1');";


	public static void main(String args[]){
		try {
			System.out.println(1 << 3);
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			//stmt.executeUpdate(query);

			/*while (rs.next()) {
				dbtime = rs.getString(1);
				System.out.println(dbtime);
			} //end while */

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}

		/*addDeveloper("Dev1");
		addProject("ArgoUML", "Java");
		addPackage("ArgoUML", "ch", "Dev1", 1);
		addPackage("ArgoUML", "ch", "Dev1", 3);
		addPackage("ArgoUML", "inf", "Dev1", 2, "ch");
		addClass("ArgoUML", "ch", "Main", "Dev1", 2);
		addClass("ArgoUML", "ch", "Main", "Dev1", 6);
		addMethod("ArgoUML", "Main", "main", "main(String[] args)", "public static", "void", "Dev1", 4);
		addAttribute("ArgoUML", "Main", "index", "index = 0", "public static", "int", 3);
		addDeveloper("Dev2");
		addBugTrackerDeveloper("Dev11", "dev11@lol.com");
		addBugTrackerDeveloper("Dev22", "dev22@lol.com");
		addBug("ArgoUML", "a strange bug", "a bug happens when opening the program", "assigned", "Dev11");
		addBugTrackerHistory("ArgoUML", "a strange bug", "Dev22");
		
		java.util.Date javaDate = new java.util.Date();
		long javaTime = javaDate.getTime();
		java.sql.Date sqlDate = new java.sql.Date(javaTime);
	    addRevision("ArgoUML", "added the project", 1, "Dev1", sqlDate.toString()); 
	    
		addClass("ArgoUML", "inf", "SubMain", "Dev1", 5);
		addInheritance("ArgoUML", "Main", "SubMain", 5); 
		addMethodMetric("ArgoUML", "main", "loc", 30, 4);
		addClassMetric("ArgoUML", "Main", "loc", 50, 6);
		addMethod("ArgoUML", "Main", "secondMain", "main(String[] args)", "public static", "void", "Dev1", 4);
		addMethod("ArgoUML", "Main", "thirdMain", "main(String[] args)", "public static", "void", "Dev1", 4);
		String[] cand = new String[1];
		cand[0] = "secondMain";
		addInvocation("ArgoUML", "main", "thirdMain", cand, 4); */

		
	}  //end main

	/**
	 * Method to add a new project into the database
	 * @param name	Name of the project
	 * @param sourceLanguage	The source language of the project
	 */
	public static void addProject(String name, String sourceLanguage){
		addProject(name, sourceLanguage, "");
	}

	/**
	 * Method to add a new project into the database
	 * @param name	Name of the project
	 * @param sourceLanguage	The source language of the project
	 * @param sourceDialect	The source dialect of the project
	 */
	public static void addProject(String name, String sourceLanguage, String sourceDialect){
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			String addProjectQuery = "INSERT INTO Projects (name, source_language, source_dialect) " +
					"VALUES ('"+name+"', '"+sourceLanguage+"', '"+sourceDialect+"');";
			stmt.executeUpdate(addProjectQuery);

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to add a new package in the database
	 * @param projectName The name of the project this packages belongs to
	 * @param name	Name of the package
	 * @param owner	The current owner of the package at the given revision number
	 * @param revisionNumber	The revision number of the package
	 */
	public static void addPackage(String projectName, String name, String owner, int revisionNumber){
		addPackage(projectName, name, owner, revisionNumber, "");
	}

	/**
	 * Method to add a new package in the database
	 * Be careful that the package this one belongs to (if any) should be already
	 * added to the database before adding this one.
	 * @param projectName The name of the project this packages belongs to
	 * @param name	Name of the package
	 * @param owner	The current owner of the package at the given revision number
	 * @param revisionNumber	The revision number of the package
	 * @param belongsToPackage	Name of the package to which this package belongs.
	 */
	public static void addPackage(String projectName, String name, String owner, int revisionNumber, String belongsToPackage){
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			//First: get the id of the owner
			String getOwnerQuery = "SELECT id FROM Developers WHERE name = '" + owner + "';";
			ResultSet rs = stmt.executeQuery(getOwnerQuery);
			int devId = 0;
			
			while (rs.next()) {
				devId = rs.getInt(1);
			}
			
			//Second: get the project id
			String getProjectIdQuery = "SELECT id FROM Projects WHERE name = '" + projectName + "'";
			rs = stmt.executeQuery(getProjectIdQuery);
			
			int projId = 0;
			while (rs.next()) {
				projId = rs.getInt(1);
			}
			
			//Belongs to no package, add it as it is.
			if(belongsToPackage == ""){
				String addPackageQuery = "INSERT INTO Packages (name, owner, revision_number, project_id) " +
						"VALUES ('"+name+"', '"+devId+"', '"+revisionNumber+"', '"+projId+"');";
				stmt.executeUpdate(addPackageQuery);
				
				//we have just to find the id
				//of the just inserted package
				String getPackageJustInsertedQuery = "SELECT * FROM Packages WHERE name = '" + name + "' AND " +
						"project_id = " + projId + " AND revision_number = " + revisionNumber;
				
				rs = stmt.executeQuery(getPackageJustInsertedQuery);
				int justInsertedId = 0;
				while (rs.next()) {
					justInsertedId = rs.getInt(1);
				}
				
				//then add it to the ProjectToPackages table
				String addToProjectToPackagesQuery = "INSERT INTO ProjectToPackages (project_id, package_id) " +
						"VALUES ("+projId+", "+justInsertedId+");";
				stmt.executeUpdate(addToProjectToPackagesQuery);
			}
			
			//Belongs to a package, add it
			else{
				//get all the packages with that name
				String getPackageIdQuery = "SELECT * FROM Packages WHERE project_id = " + projId + 
											" AND name = '" + belongsToPackage + "';";
				rs = stmt.executeQuery(getPackageIdQuery);

				//iterate through the results and find the appropriate one
				//(the one with the same rev number or the closest smaller one)
				int smallerClosest = 0;
				int packageBelongedId = 0;
				while (rs.next()) {
					int fetchedRevNum = rs.getInt("revision_number");
					//if the fetched revnum is == current revision num of the package
					//then we can store it with this connection
					if(fetchedRevNum == revisionNumber){
						//found! lucky!
						packageBelongedId = rs.getInt("id");
						break;
					} else if (fetchedRevNum <= revisionNumber){
						smallerClosest = fetchedRevNum;
						packageBelongedId = rs.getInt("id");
					}
				}
				
				//Insert the current package
				String addPackageQuery = "INSERT INTO Packages (name, owner, revision_number, project_id) " +
						"VALUES ('"+name+"', "+devId+", "+revisionNumber+", "+projId+");";
				stmt.executeUpdate(addPackageQuery);
				
				//now we have to fill the table package to package
				//we have the id of the parent package, we have just to
				//find the id of the just inserted package
				String getPackageJustInsertedQuery = "SELECT * FROM Packages WHERE name = '" + name + "' AND " +
						"project_id = " + projId + " AND revision_number = " + revisionNumber;
				rs = stmt.executeQuery(getPackageJustInsertedQuery);
				
				int justInsertedId = 0;
				while (rs.next()) {
					justInsertedId = rs.getInt(1);
				}
				
				String addToPackageToPackageTableQuery = "INSERT INTO PackageToPackages (parent_package, child_package) " +
						"VALUES ("+packageBelongedId+", "+justInsertedId+");";
				stmt.executeUpdate(addToPackageToPackageTableQuery);
			}

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to add classes to the database
	 * @param projectName	The name of the project this class belongs to
	 * @param belongsToPackage	The name of the package this class belongs to (the last in hierarchy)
	 * @param name	Name of the class
	 * @param owner	Name of the owner of the class
	 * @param revisionNumber	Revision number of the class
	 */
	public static void addClass(String projectName, String belongsToPackage, String name, String owner, int revisionNumber){
		int projId = getProjectId(projectName);
		int packageId = 0;
		if(belongsToPackage != "")
			packageId = getPackageId(belongsToPackage, projId, revisionNumber);
		int ownerId = getOwnerId(owner);
		
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			
			//first, add the class to the class table
			String addClassQuery = "INSERT INTO Classes (name, revision_number, owner, project_id) " +
					"VALUES ('"+name+"', '"+revisionNumber+"', '"+ownerId+"', + '"+projId+"');";
			stmt.executeUpdate(addClassQuery);
			
			//if it's part of a package, add the connection to the package
			//in the appropriate table
			//first, get the last class added, which is this one:
			int classId = getClassIdPreciseVersion(name, projId, revisionNumber);
			
			String addClassToPackageToClassesTableQuery = "INSERT INTO PackageToClasses (package_id, class_id) " +
					"VALUES ("+packageId+", "+classId+");";
			stmt.executeUpdate(addClassToPackageToClassesTableQuery);
			
			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	


	/**
	 * Method to add methods to the database.
	 * @param projectName	Name of the project that contains this method
	 * @param className	Name of the class that contains this method
	 * @param name	Name of the method being added to the database
	 * @param signature	Signature of the method being added to the database
	 * @param returnType	Is the return type of the method.
	 * @param owner	Is the current owner of the method for the given revision number
	 * @param revisionNumber	The revision of the method being added to the db.
	 */
	public static void addMethod(String projectName, String className, String name, String signature, String modifiers, String returnType, String owner, int revisionNumber){
		int projectId = getProjectId(projectName);
		int ownerId = getOwnerId(owner);
		
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			//add method to the database
			String addMethodQuery = "INSERT INTO Methods (name, signature, modifiers, return_type, owner, revision_number, project_id) " +
					"VALUES ('"+name+"', '"+signature+"', '"+modifiers+"', '"+returnType+"', '"+ownerId+"', '"+revisionNumber+"', '"+projectId+"');";
			stmt.executeUpdate(addMethodQuery);
			
			//get the method id and the class that contains it
			int methodId = getMethodIdPreciseVersion(name, projectId, revisionNumber);
			int containingClassId = getClassId(className, projectId, revisionNumber);
			
			//and put them into the ClassToMethods table
			String addMethodToClassToMethodsTableQuery = "INSERT INTO ClassToMethods (class_id, method_id) " +
					"VALUES ("+containingClassId+", "+methodId+");";
			stmt.executeUpdate(addMethodToClassToMethodsTableQuery);
			
			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to add attributes to the database.
	 * @param projectName	The name of the project
	 * @param className	The class name that contains the attribute
	 * @param name	The declared name of the attribute
	 * @param signature	The signature of the attribute
	 * @param modifiers	The modifiers of the attribute
	 * @param declaredType	The declared type of the attribute
	 * @param revisionNumber	The revision number of the attributed to be added to the database
	 */
	public static void addAttribute(String projectName, String className, String name, String signature, String modifiers, String declaredType, int revisionNumber){
		int projectId = getProjectId(projectName);
		
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			//add attribute to the database
			String addAttributeQuery = "INSERT INTO Attributes (name, signature, modifiers, declared_type, revision_number, project_id) " +
					"VALUES ('"+name+"', '"+signature+"', '"+modifiers+"', '"+declaredType+"', '"+revisionNumber+"', '"+projectId+"');";
			stmt.executeUpdate(addAttributeQuery);
			
			//get the attribute id and the class that contains it
			int attributeId = getAttributeIdPreciseVersion(name, projectId, revisionNumber);
			int containingClassId = getClassId(className, projectId, revisionNumber);
			
			//and put them into the ClassToMethods table
			String addAttributeToClassToAttributesTableQuery = "INSERT INTO ClassToAttributes (class_id, attribute_id) " +
					"VALUES ("+containingClassId+", "+attributeId+");";
			stmt.executeUpdate(addAttributeToClassToAttributesTableQuery);
			
			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to add a developer to the database.
	 * @param name	Name of the developer.
	 */
	public static void addDeveloper(String name){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			String addDeveloperQuery = "INSERT INTO Developers (name) " +
					"VALUES ('"+name+"');";
			stmt.executeUpdate(addDeveloperQuery);

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to add a bug tracker developer to the database.
	 * @param name	Name of the bug tracker developer
	 * @param email	Email of the developer being added
	 */
	public static void addBugTrackerDeveloper(String name, String email){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			String addBTDeveloperQuery = "INSERT INTO BugTrackerDevelopers (name, email) " +
					"VALUES ('"+name+"', '"+email+"');";
			stmt.executeUpdate(addBTDeveloperQuery);

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a bug to the table BugTrackerInfo.
	 * @param projectName	The name of the project that contains this bug
	 * @param name	The name of the bug
	 * @param desc	The description of the bug
	 * @param status	The status of the bug
	 * @param assignee	The assignee (a developer)
	 */
	public static void addBug(String projectName, String name, String desc, String status, String assignee){
		int projectId = getProjectId(projectName);
		int assigneeId = getAssigneeId(assignee);
		
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			String addBugQuery = "INSERT INTO BugTrackerInfo (name, description, belongs_to_project, status, assignee) " +
					"VALUES ('"+name+"', '"+desc+"', '"+projectId+"', '"+status+"', '"+assigneeId+"');";
			stmt.executeUpdate(addBugQuery);

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a previous assignee to the bug with name name in the project projectName
	 * @param projectName	The name of the project containing this bug
	 * @param name	The name of the bug
	 * @param previousAssignee	The name of a previous assignee
	 */
	public static void addBugTrackerHistory(String projectName, String name, String previousAssignee){
		int projectId = getProjectId(projectName);
		int previousAssigneeId = getAssigneeId(previousAssignee);
		int bugId = getBugId(projectId, name);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			String addBugHistoryQuery = "INSERT INTO BugTrackerHistory (bug_id, previous_assignee) " +
					"VALUES ('"+bugId+"', '"+previousAssigneeId+"');";
			stmt.executeUpdate(addBugHistoryQuery);

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a new revision to the database.
	 * @param projectName	The name of the project which revision is under
	 * @param comment	The comment for this particular revision
	 * @param revision_number	The revision number
	 * @param dev	The developer that committed this revision
	 * @param date	The date of the commit
	 */
	public static void addRevision(String projectName, String comment, int revision_number, String dev, String date){
		int projId = getProjectId(projectName);
		int devId = getOwnerId(dev);
		
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			String addBugQuery = "INSERT INTO Revisions (project_id, comment, revision_number, developer_id, revision_date) " +
					"VALUES ('"+projId+"', '"+comment+"', '"+revision_number+"', '"+devId+"', '"+date+"');";
			stmt.executeUpdate(addBugQuery);

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds an inheritance definition for two classes
	 * @param projectName	The name of the project
	 * @param superclass	The superclass name
	 * @param subclass	The subclass name
	 * @param versionNumber	The version number
	 */
	public static void addInheritance(String projectName, String superclass, String subclass, int versionNumber){
		int projId = getProjectId(projectName);
		int superclassId = getClassId(superclass, projId, versionNumber);
		int subclassId = getClassId(subclass, projId, versionNumber);
		
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			String addInheritanceQuery = "INSERT INTO Inheritance (subclass, superclass, revision_number) " +
					"VALUES ('"+subclassId+"', '"+superclassId+"', '"+versionNumber+"');";
			stmt.executeUpdate(addInheritanceQuery);

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a metric for a precise method into the database 
	 * @param projectName	The name of the project
	 * @param methodName	The name of the method
	 * @param metricName	The name of the metric
	 * @param value	The value of the given metric
	 * @param revisionNumber	The revision number for the current metric
	 */
	public static void addMethodMetric(String projectName, String methodName, String metricName, int value, int revisionNumber){
		int projId = getProjectId(projectName);
		int methodId = getMethodIdPreciseVersion(methodName, projId, revisionNumber);
		
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			String addMethodMetricQuery = "INSERT INTO MethodMetrics (method_id, name, value, revision_number) " +
					"VALUES ('"+methodId+"', '"+metricName+"', '"+value+"', '"+revisionNumber+"');";
			stmt.executeUpdate(addMethodMetricQuery);

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a metric for a precise class into the database
	 * @param projectName	The name of the project
	 * @param ClassName	The name of the class for which we want to store the metric
	 * @param metricName	The name of the metric
	 * @param value	The value of the metric
	 * @param revisionNumber	The revision number for which the metric will be stored
	 */
	public static void addClassMetric(String projectName, String ClassName, String metricName, int value, int revisionNumber){
		int projId = getProjectId(projectName);
		int classId = getClassIdPreciseVersion(ClassName, projId, revisionNumber);
		
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();

			String addClassMetricQuery = "INSERT INTO ClassMetrics (class_id, name, value, revision_number) " +
					"VALUES ('"+classId+"', '"+metricName+"', '"+value+"', '"+revisionNumber+"');";
			stmt.executeUpdate(addClassMetricQuery);

			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method adds the invocations and the candidates of the invokation.
	 * @param projectName	The name of the project in which the invokation happens
	 * @param receiverMethodName	The method that receives the invokation
	 * @param invokedBy	The invoker of the method
	 * @param candidates	The candidates that can be invoked by the invoker
	 * @param revisionNumber	The revision number in which all of this happens
	 */
	public static void addInvocation(String projectName, String receiverMethodName, String invokedBy, String[] candidates, int revisionNumber){
		int projId = getProjectId(projectName);
		int receiverMethodId = getMethodIdPreciseVersion(receiverMethodName, projId, revisionNumber);
		int invokerId = getMethodIdPreciseVersion(invokedBy, projId, revisionNumber);
		
		int[] candidatesId = new int[candidates.length];
		for(int i = 0; i < candidates.length; i++){
			candidatesId[i] = getMethodIdPreciseVersion(candidates[i], projId, revisionNumber);
		}
		
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			
			//put the invoked method in the table by adding the revision in which it has been invoked
			//the method itself and the invoker (invoked_by)
			String addInvocationQuery = "INSERT INTO Invocations (receiver, invoked_by, revision_number) " +
					"VALUES ('"+receiverMethodId+"', '"+invokerId+"', '"+revisionNumber+"');";
			stmt.executeUpdate(addInvocationQuery);
			
			//now we should put the candidates invoked by the invokerId
			String addCandidateQuery;
			for(int i = 0; i < candidatesId.length; i++){
				addCandidateQuery = "INSERT INTO Candidates (invoker_id, candidate, revision_number) " +
						"VALUES ("+invokerId+", "+candidatesId[i]+", "+revisionNumber+")";
				stmt.executeUpdate(addCandidateQuery);
			}
			
			con.close();
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*################################### HELPER FUNCTIONS ###################################*/
	
	/**
	 * Returns the id of the project
	 * @param projectName	The name of the project
	 * @return	The id of the project
	 */
	public static int getProjectId(String projectName){
		int projId = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			
			String getProjectIdQuery = "SELECT id FROM Projects WHERE name = '" + projectName + "'";
			ResultSet rs = stmt.executeQuery(getProjectIdQuery);
			
			while (rs.next()) {
				projId = rs.getInt(1);
			}
			
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return projId;
	}
	
	/**
	 * Returns the last packageId for the given revisionNumber.
	 * Example: revision number 3, packages in the database with this name: 1, 2, 5. This method returns 2.
	 * @param packageName	The name of the package
	 * @param projectId	The id of the project it belongs to
	 * @param revisionNumber	The revision number for which we want the closest smallest version
	 * @return	The id of the package with the above specifications.
	 */
	public static int getPackageId(String packageName, int projectId, int revisionNumber) {
		int packId = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			
			String getProjectIdQuery = "SELECT * FROM Packages WHERE name = '" + packageName + "' AND " +
					"project_id = " + projectId;
			ResultSet rs = stmt.executeQuery(getProjectIdQuery);
			
			//iterate through the results and find the appropriate one
			//(the one with the same rev number or the closest smaller one)
			int smallerClosest = 0;
			int packageBelongedId = 0;
			while (rs.next()) {
				int fetchedRevNum = rs.getInt("revision_number");
				//if the fetched revnum of the package is == current 
				//revision num of the method
				if(fetchedRevNum == revisionNumber){
					//found! lucky!
					packageBelongedId = rs.getInt("id");
					break;
				} else if (fetchedRevNum <= revisionNumber){
					smallerClosest = fetchedRevNum;
					packageBelongedId = rs.getInt("id");
				}
			}
			
			packId = packageBelongedId;
			
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return packId;
	}
	
	/**
	 * Returns the id of a developer given its name
	 * @param owner	The name of the developer
	 * @return	The id of the developer
	 */
	public static int getOwnerId(String owner){
		int ownerId = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			
			String getOwnerQuery = "SELECT id FROM Developers WHERE name = '" + owner + "';";
			ResultSet rs = stmt.executeQuery(getOwnerQuery);
			
			while (rs.next()) {
				ownerId = rs.getInt(1);
			}
			
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return ownerId;
	}
	
	/**
	 * Returns the id of the class with this precise version number
	 * in the projId project with classname name.
	 * @param name	The name of the class 
	 * @param projId	The id of the project this class belongs
	 * @param revisionNumber	The revision number we are searching for.
	 * @return The id of the class
	 */
	public static int getClassIdPreciseVersion(String name, int projId, int revisionNumber) {
		int classId = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			
			String getClassIdQuery = "SELECT * FROM Classes WHERE name = '" + name + "' AND " +
					"project_id = " + projId + " AND revision_number = " + revisionNumber;
			ResultSet rs = stmt.executeQuery(getClassIdQuery);
			
			while (rs.next()) {
				classId = rs.getInt(1);
			}
			
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return classId;
	}

	/**
	 * Fetches last classId for the given revisionNumber.
	 * Example: revision number 3, classes in the with this name: 1, 2, 5. This method returns 2.
	 * @param name	Name of the class searched
	 * @param projId	Id of the project that contains the method/class
	 * @param revisionNumber	Revision number
	 * @return
	 */
	public static int getClassId(String name, int projId, int revisionNumber){
		int classId = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			
			String getClassesIdQuery = "SELECT * FROM Classes WHERE name = '" + name + "' AND " +
					"project_id = " + projId;
			ResultSet rs = stmt.executeQuery(getClassesIdQuery);
			
			//iterate through the results and find the appropriate one
			//(the one with the same rev number or the closest smaller one)
			int smallerClosest = 0;
			int classBelongedId = 0;
			while (rs.next()) {
				int fetchedRevNum = rs.getInt("revision_number");
				//if the fetched revnum of the package is == current 
				//revision num of the method
				if(fetchedRevNum == revisionNumber){
					//found! lucky!
					classBelongedId = rs.getInt("id");
					break;
				} else if (fetchedRevNum <= revisionNumber){
					smallerClosest = fetchedRevNum;
					classBelongedId = rs.getInt("id");
				}
			}
			
			classId = classBelongedId;
			
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return classId;
	}
	
	/**
	 * Fetches the id of the method with a precise version number in a precise project with a precise name.
	 * @param methodName	The name of the method
	 * @param projId	The id of the project containing this method
	 * @param revisionNumber	The revision number searched.
	 * @return	The id of the method
	 */
	public static int getMethodIdPreciseVersion(String methodName, int projId, int revisionNumber){
		int methodId = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			
			String getMethodIdQuery = "SELECT * FROM Methods WHERE name = '" + methodName + "' AND " +
					"project_id = " + projId + " AND revision_number = " + revisionNumber;
			ResultSet rs = stmt.executeQuery(getMethodIdQuery);
			
			while (rs.next()) {
				methodId = rs.getInt(1);
			}
			
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return methodId;
	}
	
	/**
	 * Returns the id of the attribute with this precise version number, precise name and precise project id.
	 * @param attributeName	The name of the searched attribute id
	 * @param projId	The project the attribute is in
	 * @param revisionNumber	The version number of the searched attribute
	 * @return	The id of the attribute
	 */
	public static int getAttributeIdPreciseVersion(String attributeName, int projId, int revisionNumber){
		int attributeId = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			
			String getAttributeIdQuery = "SELECT * FROM Attributes WHERE name = '" + attributeName + "' AND " +
					"project_id = " + projId + " AND revision_number = " + revisionNumber;
			ResultSet rs = stmt.executeQuery(getAttributeIdQuery);
			
			while (rs.next()) {
				attributeId = rs.getInt(1);
			}
			
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return attributeId;
	}

	/**
	 * Returns the id of an assignee developer given its name
	 * @param owner	The name of the assignee developer
	 * @return	The id of the developer
	 */
	public static int getAssigneeId(String assignee){
		int assigneeId = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			
			String getAssigneeIdQuery = "SELECT id FROM BugTrackerDevelopers WHERE name = '" + assignee + "';";
			ResultSet rs = stmt.executeQuery(getAssigneeIdQuery);
			
			while (rs.next()) {
				assigneeId = rs.getInt(1);
			}
			
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return assigneeId;
	}
	
	/**
	 * Returns the id of the bug with name bugName and project id projId.
	 * @param projId	The id of the project containing the bug
	 * @param bugName	The bug name
	 * @return	The id of the searched bug
	 */
	public static int getBugId(int projId, String bugName){
		int bugId = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection (dbUrl, "root", "");
			Statement stmt = con.createStatement();
			
			String getBugIdQuery = "SELECT * FROM BugTrackerInfo WHERE name = '" + bugName + "' AND " +
					"belongs_to_project = " + projId;
			ResultSet rs = stmt.executeQuery(getBugIdQuery);
			
			while (rs.next()) {
				bugId = rs.getInt(1);
			}
			
		} //end try

		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return bugId;
	}
}  //end class