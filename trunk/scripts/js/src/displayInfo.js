function createInfo(d){
	var result = "";
	if(d.properties){
        result +="";
        for(var x in d.properties){	
        	var newName = x[0].toUpperCase();
        	if (x.length > 1)
        		newName = newName + x.substring(1);
            result += newName;
            result += ": ";
            result += d.properties[x];	
            result += "<br/>";
        }
        result += "";
    }
    
   
    if(d.metrics){
        
        for(var x in d.metrics){
        	var newName = x[0].toUpperCase();
        	if (x.length > 1)
        		newName = newName + x.substring(1);
            result += newName;
            result += ": ";
            result += d.metrics[x];	
            result += "<br/>";
        }
        result += "";
    }
    
	return result;
}