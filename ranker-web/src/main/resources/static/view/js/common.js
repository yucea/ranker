
(function($) {

	var table_stag = '<table id="${DT__ID}" class="${DT__CLASS}" style="width: 100%;">';
	var table_etag = '</table>';
	var thead_stag = '<thead>';
	var thead_etag = '</thead>';
	var th_stag = '<th${HEAD_STYLE}${HEAD_EVENT}>';
	var th_etag = '</th>';
	var default_table_class = 'table table-bordered table-hover';
	
	$.common = {
		'capitalize' : function(str){
			return str.charAt(0).toUpperCase() + str.slice(1);
		},
		'upperCase' : function(str) {
			return str.toUpperCase();
		},
		'lowerCase' : function(str) {
			return str.toLowerCase();
		},
		'toString' : function(n) {
			return n.toString();
		},
		'currency' : function(num, digits) {
			if($.isNumeric(num))
				return Number(num).toFixed(digits).replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,');
			else {
				console.warn('Non-numeric type : ', num);
				return num;
			}
		},
		'dateFormat' : function(str) {
			if(str.length == 4) return str;
			else if(str.length == 6) return str.substr(0, 4) + '.' + str.substr(4);
			else if(str.length == 8) return str.substr(0, 4) + '.' + str.substr(4, 2) + '.' + str.substr(6);
			else return str;
		},
		'toFixed' : function(v) {
			return v.toFixed(2);
		},
		'person' : function(source, separator, action) {
			var html = '';
			var arr = source.split(separator);
			for(var i in arr) {
				if(i > 0) html += ', '; 
				html += '<span class="link-person" onclick="' + action + '(\'' + arr[i] + '\')">' + arr[i] + '</span>';
			}

			return html;
		}
	}

	$.datatable = {
		/* - COLUMN attributes
		  key : data id
		  value : javascript function
		  hstyle : th style
		  style : td style
		  func : td value convert javascript function
		 */
			
		'lengthMenu' : [ 
			[ 10, 20, 50, 100 ], // value
			[ 10, 20, 50, 100 ] // name
		],
		'display' : function(option) {
			var url = option.url;
			var widgetId = option.id;
			var tableId = option.id + '_table';
			var columns = option.columns;
			var clazz = option.clazz;
			var listNum = option.listNum;
			var search = option.search == null ? true : option.search;
			
			if(option.lengthMenu) this.lengthMenu = option.lengthMenu;
			
			this.displayHead(widgetId, tableId, columns, clazz);
			this.displayBody(url, tableId, columns, listNum, search);
		},
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
				var column = columns[i];
				var style = this.hasStyleHeader(column) ? ' style="' + column.head.style + '"' : '';
				var event = this.getEventHeader(column);
				
				html += (th_stag.replace('${HEAD_STYLE}', style).replace('${HEAD_EVENT}', event)) + this.getHeaderValue(column) + th_etag;
			}
			
			html += thead_etag;
			html += table_etag;
			
			return html;
		},
		'hasStyleHeader' : function(column) {
			return column.head && column.head.style;
		},
		'hasEventHeader' : function(column) {
			return column.head && (column.head.event || column.head.events);
		},
		'hasFuncHeader' : function(column) {
			return column.head && column.head.func != null && $.isFunction(column.head.func);
		},
		'getEventHeader' : function(column) {
			if(this.hasEventHeader(column)) {
				if(column.head.event) {
					return ' ' + column.head.event.name + '=' + column.head.event.value;
				} else if(column.head.events) {
					var value = '';
					for(var j in column.head.events) {
						value += ' ' + column.head.events[j].name + '=' + column.head.events[j].value;
					}
					return value;
				}
			} else {
				return '';
			}
		},
		'getHeaderValue' : function(column) {
			return this.hasFuncHeader(column) ? column.head.func(column.name) : column.name;
		},
		'columns' : function(columns) {
			var data = [];
			for(var i in columns) {
				var col = new Object();
				col.data = columns[i].key ? columns[i].key : columns[i].name;
				
				data.push(col);
			}

			return data;
		},
		'displayBody' : function(url, tableId, columns, listNum, search) {
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
		        "searching" : search,		// Search Box will Be Disabled
		        "ordering" : false,     // Ordering (Sorting on Each Column)will Be Disabled
		        "info" : true,          // Will show "1 to n of n entries" Text at bottom
		        "lengthChange" : this,	// Will Disabled Record number per page
		        "serverSide" : true,
		        "paginate" : true,
		        "pageLength" : listNum == null ? 10 : listNum,
		        "lengthMenu" : this.lengthMenu,
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
					// "info": '전체 : _TOTAL_건',
					"search" : '<span class="input-group-addon"><i class="glyphicon glyphicon-search"></i></span>'
				},
				"rowCallback" : function(row, data, index) {
					for(var i in columns) {
						var column = columns[i];
						var value = eval('data.' + (column.key ? column.key : column.name));
						
						if(column.body) {
							if(column.body.event)
								$('td:eq(' + i + ')', row).attr(column.body.event.name, self.findValue(column.name, data, column.body.event.value));
							if(column.body.events)
								for(var j in column.body.events)
									$('td:eq(' + i + ')', row).attr(column.body.events[j].name, self.findValue(column.name, data, column.body.events[j].value));
							if(column.body.style && column.body.style) {
								var styles = column.body.style.split(';');
								for(var j in styles) {
									var opt = styles[j].split(':');
									if(opt != '')
										$('td:eq(' + i + ')', row).css(opt[0].trim(), opt[1].trim());
								}
							}
							if(column.body.func)
								value = column.body.func(value);
						}
						
						var html = '';
						if(column.data) {
							var style = '';
							var event = '';
							if(column.data.event)
								event = ' ' + column.data.event.name + '="' + self.findValue(column.name, data, column.data.event.value.replace(/"/g, "'")) + '"';
							if(column.data.events)
								for(var j in column.data.events)
									event += ' ' + column.data.events[j].name + '="' + self.findValue(column.name, data, column.data.events[j].value.replace(/"/g, "'")) + '"'; 
							if(column.data.style)
								style = ' style=\'' + column.data.style + '\'';
							if(column.data.func)
								value = column.data.func(value);
							
							html = '<span ' + event + style + '>' + value + ' </span>'
						}
						
						$('td:eq(' + i + ')', row).html(html == '' ? value : html);
					}
				}
			});
		},
		'findValue' : function(key, data, script) {
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



