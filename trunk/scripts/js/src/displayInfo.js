function createInfo(d){
	var result = "";
	if(d.properties){
        result +="<div id='dispProperties'>";
        for(var x in d.properties){
            result += x;
            result += ": ";
            result += d.properties[x];	
            result += " ";
        }
        result += "</div>";
    }
    
   
    if(d.metrics){
        result +="<div id='dispMetrics'>";
        
        for(var x in d.metrics){
            result += x;
            result += ": ";
            result += d.metrics[x];	
            result += " ";
        }
        result += "</div>";
    }
    
	return result;
}