# tableSorter
tableSorter is a jQuery plugin that allows an html table to be sorted by clicking on the header cells. 

## Prepare the html table 
- add 'sorter-header' class to table header row (tr) and classes: 'no-sort', 'is-date', 'is-number' and 'case-sensitive' to the header cells (th) to indicate how to sort the columns. Case insensitive is the default. 
```html
<table class="sortable-table indexed">
    <tbody>
        <tr class="sorter-header">
            <th class="no-sort">&nbsp;</th>
            <th>First</th>
            <th>Last</th>
            <th class="is-date">Date</th>
            <th class="case-sensitive">CS</th>
            <th>CI</th>
            <th class="is-number">Number</th>
        </tr> ....
```

## Initialise 
- Include http://code.jquery.com/jquery-latest.min.js & tableSorter.js.
- Initialise tableSorter.
```html
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script src="./js/table-sorter.js"></script>
<script> // better to use an external JavaScript file
  $().ready(function(){
    $('.sortable-table').tableSorter(); 
  });
</script>
```
## Creating an index column
To create an index column (based only on CSS) add the class 'indexed' to the table, each table row (tr) that needs to be indexed and the table cell (td) where the index needs to go. The table cell need to have an empty span.
```html
<table class="sortable-table indexed">
.....
<tr class="indexed">
    <td class="indexed">
        <span>&nbsp;</span>
    </td>
    <td> ....

```
