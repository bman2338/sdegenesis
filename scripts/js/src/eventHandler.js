var eventHandler = EventHandler();

//{obj:object, hand:handler, typ:type, running:true}
//	the target, called function, onclick... if it is paused or not
//  d3.select(window) .on("keydown",    function() 


function EventHandler(){
	var handlers = {
		window: {
			defaults: {
				keydown: undefined;
			}
			values: {
			}
	};
	return{
		pause: function(){
			for(hndlr in handlers){
				d3.select(hndlr).on(hndlr.values, hndlr.values.handler).remove();
			}
		};
		
		resume: function(){};
		
		add: function(object, handler, type){
			if (!handlers[object])
				handlers[object] = { defaults: {}, values: {} };
			handlers[object].values[type] = handler;
			//handlers.push({obj:object, hand:handler, typ:type, running:true});
			d3.select(object).on(type,handler);
		};
		remove: function(){};
	}
}