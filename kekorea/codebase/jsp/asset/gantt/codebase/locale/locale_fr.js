/*
@license

dhtmlxGantt v.6.2.2 Professional Evaluation
This software is covered by DHTMLX Evaluation License. Contact sales@dhtmlx.com to get Commercial or Enterprise license. Usage without proper license is prohibited.

(c) Dinamenta, UAB.

*/
Gantt.plugin(function(e){!function(e,t){if("object"==typeof exports&&"object"==typeof module)module.exports=t();else if("function"==typeof define&&define.amd)define([],t);else{var n=t();for(var r in n)("object"==typeof exports?exports:e)[r]=n[r]}}(window,function(){return function(e){var t={};function n(r){if(t[r])return t[r].exports;var o=t[r]={i:r,l:!1,exports:{}};return e[r].call(o.exports,o,o.exports,n),o.l=!0,o.exports}return n.m=e,n.c=t,n.d=function(e,t,r){n.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:r})},n.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},n.t=function(e,t){if(1&t&&(e=n(e)),8&t)return e;if(4&t&&"object"==typeof e&&e&&e.__esModule)return e;var r=Object.create(null);if(n.r(r),Object.defineProperty(r,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var o in e)n.d(r,o,function(t){return e[t]}.bind(null,o));return r},n.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return n.d(t,"a",t),t},n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},n.p="/codebase/",n(n.s=207)}({207:function(t,n){e.locale={date:{month_full:["Janvier","Février","Mars","Avril","Mai","Juin","Juillet","Août","Septembre","Octobre","Novembre","Décembre"],month_short:["Jan","Fév","Mar","Avr","Mai","Juin","Juil","Aoû","Sep","Oct","Nov","Déc"],day_full:["Dimanche","Lundi","Mardi","Mercredi","Jeudi","Vendredi","Samedi"],day_short:["Dim","Lun","Mar","Mer","Jeu","Ven","Sam"]},labels:{new_task:"Nouvelle tâche",new_event:"Nouvel évènement",icon_save:"Enregistrer",icon_cancel:"Annuler",icon_details:"Détails",icon_edit:"Modifier",icon_delete:"Effacer",confirm_closing:"",confirm_deleting:"L'événement sera effacé sans appel, êtes-vous sûr ?",section_description:"Description",section_time:"Période",section_type:"Type",column_wbs:"OTP",column_text:"Nom de la tâche",column_start_date:"Date initiale",column_duration:"Durée",column_add:"",link:"Le lien",confirm_link_deleting:"sera supprimé",link_start:"(début)",link_end:"(fin)",type_task:"Task",type_project:"Project",type_milestone:"Milestone",minutes:"Minutes",hours:"Heures",days:"Jours",weeks:"Semaines",months:"Mois",years:"Années",message_ok:"OK",message_cancel:"Annuler",section_constraint:"Constraint",constraint_type:"Constraint type",constraint_date:"Constraint date",asap:"As Soon As Possible",alap:"As Late As Possible",snet:"Start No Earlier Than",snlt:"Start No Later Than",fnet:"Finish No Earlier Than",fnlt:"Finish No Later Than",mso:"Must Start On",mfo:"Must Finish On",resources_filter_placeholder:"type to filter",resources_filter_label:"hide empty"}}}})})});