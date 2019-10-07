
  var mysql = require('mysql');

  var con = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "123456",
    database: "authproj"
  });



  con.connect(function(err) {
    if (err) throw err;
    con.query("UPDATE student SET VoicePrint='1' WHERE StudentID='virtual'", function (err, result, fields) {
      if (err) throw err;
      console.log(result);
    });
  });
