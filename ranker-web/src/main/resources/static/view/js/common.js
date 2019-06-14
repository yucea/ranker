
(function($) {

	var table_stag = '<table id="${DT__ID}" class="${DT__CLASS}" style="width: 100%;">';
	var table_etag = '</table>';
	var thead_stag = '<thead>';
	var thead_etag = '</thead>';
	var th_stag = '<th>';
	var th_etag = '</th>';
	var default_table_class = 'table table-bordered table-hover';
	
	$.common = {
		'capitalize' : function(str1){
			return str1.charAt(0).toUpperCase() + str1.slice(1);
		}
	}

	$.datatable = {
		
		'displayHead' : function(widgetId, tableId, columns, clazz) {
			var html = this.header(tableId, columns, clazz);
			$('#' + widgetId).html(html);
		},
		'header' : function(tableId, columns, clazz) {
			var html = table_stag
				.replace('${DT__ID}', tableId)
				.replace('${DT__CLASS}', clazz == null ? default_table_class : clazz);
			
			html += thead_stag;
			
			for(var i in columns) {
				var key = Object.getOwnPropertyNames(columns[i])[0];
				var value = columns[i][key];
				
				html += th_stag + $.common.capitalize(key) + th_etag;
			}
			
			html += thead_etag;
			html += table_etag;
			
			return html;
		},
		'columns' : function(columns) {
			var data = [];
			for(var i in columns) {
				var col = new Object();
				col.data = Object.getOwnPropertyNames(columns[i])[0];
				
				data.push(col);
			}

			return data;
		},
		'displayBody' : function(url, tableId, columns) {
			/* // DOM Position key index //
			
				l - Length changing (dropdown)
				f - Filtering input (search)
				t - The Table! (datatable)
				i - Information (records)
				p - Pagination (paging)
				r - pRocessing 
				< and > - div elements
				<"#id" and > - div with an id
				<"class" and > - div with a class
				<"#id.class" and > - div with an id and class
				
				Also see: http://legacy.datatables.net/usage/features
			 */
			var self = this;

			$('#' + tableId).dataTable({
		        "searching" : true,		// Search Box will Be Disabled
		        "ordering" : false,     // Ordering (Sorting on Each Column)will Be Disabled
		        "info" : true,          // Will show "1 to n of n entries" Text at bottom
		        "lengthChange" : this,	// Will Disabled Record number per page
		        "serverSide" : true,
		        "paginate" : true,
		        "lengthMenu" : [ [ 10, 20, 50, 100 ], [ 10, 20, 50, 100 ] ],
				"ajax" : {
					'url' : url,
					'type' : 'POST'
				},
				"columns" : this.columns(columns),
				"dom" : "<'dt-toolbar'<'col-xs-12 col-sm-6'f>r<'col-sm-6 col-xs-12 hidden-xs'l>>"+
					"t"+
					"<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
				"language" : {
					// "lengthMenu": "리스트 개수 : _MENU_",
					// "info": 'Total count : _TOTAL_건',
					"search" : '<span class="input-group-addon"><i class="glyphicon glyphicon-search"></i></span>'
				},
				"rowCallback" : function(row, data, index) {
					for(var i in columns) {
						var keys = Object.getOwnPropertyNames(columns[i]);
						var key = keys[0];
						
						var script = columns[i][key];
						if(script != '') {
							$('td:eq(' + i + ')', row).css('cursor', 'pointer');
							$('td:eq(' + i + ')', row).attr('onclick', self.findValue(key, data, script));
						}
						
						if(keys[1] != null) { // style
							var styles = columns[i][keys[1]].split(';');
							for(var j in styles) {
								var opt = styles[j].split(':');
								if(opt != '')
									$('td:eq(' + i + ')', row).css(opt[0].trim(), opt[1].trim());
							}
						}
					}
				}
			});
		},
		'findValue' : function(key, data, script) {
			if(script.indexOf('_') == -1) return;
			
			var keys = Object.getOwnPropertyNames(data);
			for(var i in keys) {
				var skey = '_' + keys[i].toUpperCase() + '_';
				if(script.search(skey) != -1) {
					script = script.replace(skey, '\'' + eval('data.' + keys[i]) + '\'');
				}
			}
			
			return script;
		}
		
	}
	
})(jQuery);



