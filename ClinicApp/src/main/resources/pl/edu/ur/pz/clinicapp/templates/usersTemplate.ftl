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
    <h2>20 najnowszych użytkowników</h2>
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
                    <td>${user.getDisplayName()}</td>
                    <#if user.getPhone()?has_content>
                        <td>${user.getPhone()}</td>
                    <#else>
                        <td></td>
                    </#if>
                    <#if user.getEmail()?has_content>
                        <td>${user.getEmail()}</td>
                    <#else>
                        <td></td>
                    </#if>
                </tr>
        </#list>
        </tbody>
    </table>
    <br>

    <p>Wygenerowano ${.now?string("dd.MM.yyyy hh:mm")}</p>
</div>
</html>
