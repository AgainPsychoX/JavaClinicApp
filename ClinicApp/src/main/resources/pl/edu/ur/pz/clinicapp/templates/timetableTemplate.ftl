<html lang="pl">
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


<div>

    <h2>Terminarz</h2>
        <img src="${timetable}" alt="Terminarz">
    <br>

    <p>Wygenerowano ${.now?string("dd.MM.yyyy hh:mm")}</p>
</div>
</body>
</html>
