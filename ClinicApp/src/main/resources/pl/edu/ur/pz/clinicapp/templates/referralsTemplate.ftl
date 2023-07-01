<html lang="pl">
<head>
    <meta charset="UTF-8">

    <style>
        @page{
            size: landscape;
        }
        body{
            font-family: Calibri;
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
<div>
    <h2>Badania</h2>
    <#if startDate?has_content && endDate?has_content>
        <h3>Zakres dat: ${startDate} - ${endDate}</h3>
    </#if>
    <table class="table-style">
        <thead>
        <tr>
            <#list headers as header>
                <th>${bundle["referral." + header]}</th>
            </#list>

        </tr>
        </thead>
        <tbody>
        <#list referrals as referral>
            <#assign formattedAddedDate = DateUtils.toLocalDateTime(referral.addedDate)>
            <#assign startDiff = 0>
            <#assign endDiff = 0>
            <#if startDate?has_content && endDate?has_content>
                <#assign date = formattedAddedDate?date("yyyy-MM-dd")?long>
                <#assign startDiff = startDate?date("yyyy-MM-dd")?long - date>
                <#assign endDiff = endDate?date("yyyy-MM-dd")?long - date>
            </#if>
            <#if (startDiff <= 0) && (endDiff >= 0)>
                <tr>
                    <#list headers as field>
                        <#if field == "patient">
                            <td>${referral.patient.name} ${referral.patient.surname}</td>
                        <#elseif field == "addedBy">
                            <td>${referral.getDoctorName()?string}</td>
                        <#elseif field == "tags">
                            <td>${referral.getStringTags()?string}</td>
                        <#elseif field == "notes">
                            <td>
                                <#list referral.notes?split(";") as note>
                                    ${note}<br>
                                </#list>
                            </td>
                        <#elseif field == "addedDate">
                            <td>${referral.getAddedDateFormatted()}</td>
                        <#elseif field == "fulfilmentDate">
                            <td>${referral.getFulfilmentDateFormatted()?default('')}</td>
                        <#else>
                            <td>${referral[field]?default('')}</td>
                        </#if>
                    </#list>
                </tr>
            </#if>
        </#list>
        </tbody>
    </table>
    <br>

    <p>Wygenerowano ${.now?string("dd.MM.yyyy hh:mm")}</p>
</div>
</body>
</html>
