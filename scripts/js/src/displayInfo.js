function createInfo(d){
	var result = "";
	
	for(x in d){
		result += x;
		result += ": ";
		result += d[x];	
		result += " ";
	}
    
	return result;
}