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

        .info {
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

        .display {
            display: flex;
            -webkit-box-pack: center;
            justify-content: center;
            margin: 0 auto;
            width: 100%;
        }

        h2 {
            font-size: 2rem;
        }

        h3 {
            font-size: 1.75rem;
        }

        h1, h2, h3 {
            font-weight: 500;
            line-height: 1.2;
        }

        .information {
            display: flex;
            margin-bottom: 20px;
            font-size: 24px;
        }

        .referral {
            display: -webkit-box;
            display: flex;
            -webkit-box-orient: vertical;
            -webkit-box-direction: normal;
            flex-direction: column;
            /*padding: 24px 0;*/
            font-size: 24px;
        }
    </style>
</head>
<body>

</body>
<#assign formattedAddedDate = DateUtils.toLocalDateTime(referral.addedDate)>
<div class="display">
    <div class="content">
        <h2>Badanie</h2>
        <div class="info">
            <h3>
                <p>Badanie wykonano: <b>${referral.getAddedDateFormatted()}</b></p>
                <p>Lekarz: <b>${referral.getDoctorName()?string}</b></p>
            </h3>
        </div>
        <div class="information">
            <div style="width:200px"> Przebieg:</div>
            <div class="referral">
                <#list referral.notes?split(";") as note>
                    ${note}<br>
                </#list>
            </div>
        </div>
        <#if referral.feedback?has_content>
            <div class="information">
                <div style="width:200px"> Opinia:</div>
                <div class="referral">
                    ${referral.feedback}
                </div>
            </div>
        </#if>
        <#if referral.fulfilmentDate?has_content>
            <p>Data realizacji badania: ${referral.getFulfilmentDateFormatted()}</p>
        </#if>
    </div>
</div>
</html>
