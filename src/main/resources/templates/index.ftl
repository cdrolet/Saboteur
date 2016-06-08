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
    <table class="table">
        <tr>
            <th>Description</th>
            <th>Path</th>
            <th>State</th>
        </tr>
        <#list model["targets"] as target>
            <tr>
                <td>${target.alias}</td>
                <td>${target.classPath}</td>
                <td>${target.disrupted?string('active', 'inactive')}</td>
            </tr>
        </#list>

    </table>
</div>
<div class="container">
  <div class="alert alert-info text-center" role="alert">
  BlaBlaBla
  </div>
</div>

</body>

</html>