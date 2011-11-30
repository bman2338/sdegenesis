function data() { 
    var json = { 
        "name": "", "children": [
            { "name": "BugTrackerUser", "children": [
                { "name": "BugTrackerUser"},
                { "name": "BugTrackerUser"},
                { "name": "getName"},
                { "name": "setName"},
                { "name": "getDisplayName"},
                { "name": "setDisplayName"},
                { "name": "getEmail"},
                { "name": "setEmail"},
                { "name": "toString"},]
            },
            { 
            "name": "JiraCrawler", "children": [
                { "name": "JiraCrawler"},
                { "name": "JiraCrawler"},
                { "name": "getBugList"},]
            },
            { 
            "name": "BugTrackerCrawler", "children": [
                { "name": "getBugList"},]
            },
            { "name": "BugzillaCrawler", "children": [
                { "name": "BugzillaCrawler"},
                { "name": "retrieveBugId"},
                { "name": "concatenateURLs"},
                { "name": "getBugList"},]
            },
            { "name": "BugzillaXMLParser", "children": [
                { "name": "parse"},]
            },
            { 
            "name": "Environment", "children": [
                { "name": "addDefinition"},
                { "name": "getDefinition"},]
            },
            { 
            "name": "LanguageFactory", "children": [
            { "name": "getLanguage"},]
            },]};
    return json;
}