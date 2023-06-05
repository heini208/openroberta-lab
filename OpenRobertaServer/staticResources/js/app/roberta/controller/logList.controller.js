define(["require","exports","util.roberta","guiState.controller","jquery","bootstrap-table"],(function(t,e,o,a,i){function l(){i("#logTable").bootstrapTable({locale:a.getLanguage(),pageList:"[ 10, 25, All ]",toolbar:"#logListToolbar",theadClasses:"table-dark",showRefresh:"true",showPaginationSwitch:"true",pagination:"true",buttonsAlign:"right",rowStyle:n,iconsPrefix:"typcn",icons:{paginationSwitchDown:"typcn-document-text",paginationSwitchUp:"typcn-book",refresh:"typcn-delete"},columns:[{title:"no.",sortable:!0,align:"center",width:"75px",field:"0"},{title:"type",sortable:!0,align:"center",width:"75px",field:"1"},{title:"message",field:"2"}]}),i("#logTable").bootstrapTable("togglePagination"),i("#logList>.bootstrap-table").find('button[name="refresh"]').attr("title","").attr("rel","tooltip").attr("data-placement","left").attr("lkey","Blockly.Msg.BUTTON_EMPTY_LIST").attr("data-bs-original-title",Blockly.Msg.BUTTON_EMPTY_LIST).tooltip("_fixTitle"),i("#tabLogList").onWrap("show.bs.tab",(function(){a.setView("tabLogList")})),i(window).resize((function(){i("#logTable").bootstrapTable("resetView",{height:o.calcDataTableHeight()})})),i("#logList>.bootstrap-table").find('button[name="refresh"]').onWrap("click",(function(){return i("#logTable").bootstrapTable("removeAll"),!1}),"empty log list clicked"),i("#backLogList").onWrap("click",(function(){return i("#"+a.getPrevView()).tabWrapShow(),!1}),"back to previous view")}function n(t,e){return"[[ERR ]] "===t[1]?{classes:"danger"}:{}}Object.defineProperty(e,"__esModule",{value:!0}),e.switchLanguage=e.init=void 0,e.init=l,e.switchLanguage=function(){i("#logTable").bootstrapTable("destroy"),l()}}));
//# sourceMappingURL=logList.controller.js.map
//# sourceMappingURL=logList.controller.js.map
