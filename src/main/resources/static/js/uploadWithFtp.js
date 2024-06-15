$('body').on('click', '#ftpUpload', function() {
    var api_path = "/uploadToFtp";
    var login = document.getElementById('ftpLogin').value;
    var password = document.getElementById('ftpPassword').value;
    var ip = document.getElementById('ftpIp').value;
    var port = document.getElementById('ftpPort').value;
    var folder = document.getElementById('ftpFolder').value;
    $.ajax({
        type: "POST",
        url: api_path,
        data: {"login" : login, "password" : password, "ip" : ip, "port" : port, "folder" : folder},
        success:function(result) {
            console.log("FTP uploading result: " + result); 
        },
    });
});