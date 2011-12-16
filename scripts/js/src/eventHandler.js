var eventHandler = EventHandler();

//{obj:object, hand:handler, typ:type, running:true}
//	the target, called function, onclick... if it is paused or not
//  d3.select(window) .on("keydown",    function() 


function EventHandler(){
	var handlers = {
	};
	return{
		pause: function(){
			for(var hndlr in handlers){
				for (var value in handlers[hndlr].defaults) {
					d3.select(hndlr).on(value, handlers[hndlr].defaults[value]);
				}
			}
		},
		
		resume: function(){
			for(var hndlr in handlers){
				for (var value in handlers[hndlr].values) {
					d3.select(hndlr).on(value, handlers[hndlr].values[value]);
				}
			}
		},
		
		add: function(object, type, handler){
			if (!handlers[object])
				handlers[object] = { defaults:{ keydown: null, }, values: {} };
			handlers[object].values[type] = handler;
			//handlers.push({obj:object, hand:handler, typ:type, running:true});
			d3.select(object).on(type,handler);
		},
		remove: function(){},
	}
}
