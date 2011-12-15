function createInfo(d){
	var result = "";
	if(d.properties){
        result +="";
        for(var x in d.properties){
            result += x;
            result += ": ";
            result += d.properties[x];	
            result += "<br>";
        }
        result += "";
    }
    
   
    if(d.metrics){
        
        for(var x in d.metrics){
            result += x;
            result += ": ";
            result += d.metrics[x];	
            result += "<br>";
        }
        result += "";
    }
    
	return result;
}