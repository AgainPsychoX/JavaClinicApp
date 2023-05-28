<html lang="pl">
<#--WIP-->
<head>
    <meta charset="UTF-8">

    <style>
        body {
            font-family: Calibri, sans-serif;
        }

        .table-style {
            border-collapse: collapse;
            width: 100%;
        }

        .table-style td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }

        .table-style th {
            border: 1px solid #ddd;
            padding: 8px;
            background-color: #f2f2f2;
            font-weight: bold;
            text-align: center;
        }

        .table-style tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        .table-style tr:hover {
            background-color: #ddd;
        }
    </style>
</head>
<body>

</body>
<div>
    <h2>Lista recept</h2>
    <#if startDate?has_content && endDate?has_content>
        <h3>Zakres dat: ${startDate} - ${endDate}</h3>
    </#if>
    <table class="table-style">
        <thead>
        <tr>
            <#list headers as header>
                <th>${bundle["prescription." + header]}</th>
            </#list>

        </tr>
        </thead>
        <tbody>
        <#list users as user>
                <tr>
                    <#list headers as field>
                        <#if field == "name">
                            <td>${user.patient.name} ${user.patient.surname}</td>
                        <#elseif field == "addedBy">
                            <td>${user.getDoctorName()?string}</td>
                        <#elseif field == "tags">
                            <td>${user.tags}</td>
                        <#elseif field == "notes">
                            <td>
                                <#list user.notes?split(";") as note>
                                    ${note}<br>
                                </#list>
                            </td>
                        <#elseif field == "addedDate">
                            <td>${user.addedDate?string("dd.MM.yyyy")}</td>
                        <#else>
                            <td>${user[field]?default('')}</td>
                        </#if>
                    </#list>
                </tr>
        </#list>
        </tbody>
    </table>
    <br>

    <p>Wygenerowano ${.now?string("dd.MM.yyyy hh:mm")}</p>
</div>
</html>
