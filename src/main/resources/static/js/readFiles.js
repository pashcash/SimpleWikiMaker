$('body').on('click', '#pickFilesButton', async (evt) => {
    const out = {};
    const dirHandle = await showDirectoryPicker();
    const fileList = [];
    const settingsList = [];
    await handleDirectoryEntry("", dirHandle, out, fileList, settingsList);
    var apiPath = "/setListOfMdFiles";
    $.ajax({
        type: "POST",
        url: apiPath,
        data: JSON.stringify(fileList),
        contentType:"application/json; charset=utf-8",
        dataType: "text",
        success:function(result) {
            $("#mdFiles").html(result);
            console.log("MD files fill");
            console.log(fileList);
            var apiPath = "/setSettingsFiles";
            $.ajax({
                type: "POST",
                url: apiPath,
                data: JSON.stringify(settingsList),
                contentType:"application/json; charset=utf-8",
                dataType: "text",
                success:function() {
                    console.log(settingsList);
                    var api_path = "/getHtmlFolder";
                    $.ajax({
                        type: "POST",
                        url: api_path,
                        success:function(result) {
                            $("#htmlFiles").html(result);
                            console.log("HTML files fill");
                            var path = "/defaultPageFrame";
                            $.ajax({
                                type: "GET",
                                url: path,
                                success:function(result) {
                                    $("#frameDiv").html(result);
                                    console.log("Preview fill"); 
                                },
                            }); 
                        },
                    });
                },
            });
        },
    });
});

async function handleDirectoryEntry(filePath, dirHandle, out, fileList, settingsList) {
    for await (const entry of dirHandle.values()) {
        if (entry.kind === "file") {
            const file = await entry.getFile();
            if (file.name.split(".").pop() === "md") {
                const fileContent = await file.text();
                out[file.name] = {};
                const fileStr = fillFileString(fileContent, file.name, filePath, "file");
                fileList.push(fileStr);
            }
            else if (file.name === "theme.css") {
                const fileContent = await file.text();
                const settingStr = fillFileString(fileContent, file.name, filePath, "theme");
                settingsList.push(settingStr);
            }
            else if (file.name === "info.txt") {
                const fileContent = await file.text();
                const settingStr = fillFileString(fileContent, file.name, filePath, "settings");
                settingsList.push(settingStr);
            }
        }
        else if (entry.kind === "directory") {
            const newHandle = await dirHandle.getDirectoryHandle(entry.name, { create: false, });
            if (newHandle.name !== ".obsidian")
            {
                const newOut = (out[entry.name]={});
                const fileStr = fillFileString(null, newHandle.name, filePath, "dir");
                fileList.push(fileStr);
                await handleDirectoryEntry(filePath + "/" + newHandle.name, newHandle, newOut, fileList, settingsList);
            }
        }
    }
}

function fillFileString(fileContent, fileName, filePath, fileType) {
    const fileObject = {};
    fileObject["name"] = fileName;
    fileObject["text"] = fileContent;
    fileObject["type"] = fileType;    
    if (fileType === "file" || fileType === "dir")
    {
        fileObject["path"] = filePath + "/" + fileName;
    }
    return fileObject;
}

$('body').on('click', '#mdBackButton', function() {
    var api_path = "/mdBackButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        success:function(result) {
            $("#mdFiles").html(result);
            console.log("MD files fill");
        },
    });
});

$('body').on('click', '#htmlBackButton', function() {
    var api_path = "/htmlBackButtonClick";
    $.ajax({
        type: "POST",
        url: api_path,
        success:function(result) {
            $("#htmlFiles").html(result);
            console.log("HTML files fill");
        },
    });
});

$(document).ready(function() {
    console.log("Ready!");
    var api_path = "/getHtmlFolder";
    $.ajax({
        type: "GET",
        url: api_path,
        success:function(result) {
            $("#htmlFiles").html(result);
            console.log("HTML files fill");
        },
    });
    var api_path = "/getMdFolder";
    $.ajax({
        type: "GET",
        url: api_path,
        success:function(result) {
            $("#mdFiles").html(result);
            console.log("MD files fill");
        },
    });
    var api_path = "/defaultPageFrame";
    $.ajax({
        type: "GET",
        url: api_path,
        success:function(result) {
            $("#frameDiv").html(result);
            console.log("Preview fill"); 
        }
    })
});