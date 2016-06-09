<!DOCTYPE html>
<html>
<head>
    <#include "header.ftl"/>
</head>

<body>

<#include "nav.ftl"/>


  <div style="text-align:center" class="container">
    <h1>Saboteur</h1>
  </div>


<div class="container">
    <table id="effects" class="table">
        <tr>
            <th>Description</th>
            <th>Path</th>
            <th>State</th>
        </tr>
        <#list model["actions"] as action>
            <tr>
                <td>${action.name}</td>
                <td>${action.targetClassPath}</td>
                <td>${action.active?string('active', 'inactive')}</td>
            </tr>
        </#list>

    </table>
</div>

</body>

</html>
