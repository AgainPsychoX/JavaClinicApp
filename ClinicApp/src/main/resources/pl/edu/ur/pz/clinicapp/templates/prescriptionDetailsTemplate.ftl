<html lang="pl">
<head>
    <meta charset="UTF-8">

    <style>
        body {
            font-family: Calibri, sans-serif;
            font-size: 1rem;
            font-weight: 400;
            line-height: 1.5;
            text-align: left;
        }
        .content {
            background-color: #fff;
            border: 1px solid #dbdbdb;
            color: #000;
            margin: 0 36px;
            padding: 50px 64px;
            width: 990px;
        }
        .codeDate{
            -webkit-box-align: center;
            align-items: center;
            border-bottom: 2px solid #000;
            display: -webkit-box;
            display: flex;
            -webkit-box-pack: justify;
            justify-content: space-between;
            margin-bottom: 25px;
            padding-bottom: 25px;
        }
        .display{
            display: flex;
            -webkit-box-pack: center;
            justify-content: center;
            margin: 0 auto;
            width: 100%;
        }
        h2{
            font-size: 2rem;
        }
        h3{
            font-size: 1.75rem;
        }
        h1, h2, h3{
            font-weight: 500;
            line-height: 1.2;
        }
        .person{
            display: flex;
            margin-bottom: 20px;
            font-size: 24px;
        }
        .prescription{
            border-bottom: 2px solid #000000;
            border-top: 2px solid #000000;
            display: -webkit-box;
            display: flex;
            -webkit-box-orient: vertical;
            -webkit-box-direction: normal;
            flex-direction: column;
            padding: 24px 0;
        }
    </style>
</head>
<body>

</body>
<#assign formattedAddedDate = DateUtils.toLocalDateTime(prescription.addedDate)>
<div class="display">
    <div class="content">
        <h2>e recepta</h2>
        <div class="codeDate">
            <h3>
                Kod recepty: <b>${prescription.governmentId}</b>
                <p>Wystawiono: <b>${prescription.getAddedDateFormatted()}</b></p>
            </h3>
        </div>
        <div class="person">
            <div style="width:200px"> Pacjent: </div>
            <div style="align-self: end"> ${prescription.patient.name} ${prescription.patient.surname}</div>
        </div>
        <div class="person">
            <div style="width:200px"> Wystawca: </div>
            <div style="align-self: end"> ${prescription.getDoctorName()?string}</div>
        </div>
        <div class="prescription">
            <#list prescription.notes?split(";") as note>
                ${note}<br>
            </#list>
        </div>
        <p>Przy realizacji recepty podaj kod recepty oraz swój numer PESEL.</p>
    </div>
</div>
</html>
