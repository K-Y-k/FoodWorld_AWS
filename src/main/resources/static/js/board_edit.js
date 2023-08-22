var attachedFiles = $(".attachedFile");
var imageFiles = $(".imageFile");

var inputAttachedFile = $("#input-attachFile");
var inputImageFile = $("#input-imageFile");

inputAttachedFile.on('change', (event) => {
    attachedFiles.empty();

    var attFiles = inputAttachedFile[0].files;
    console.log("inputAttachedFile[0].files", inputAttachedFile[0].files)

    for(var i = 0; i < attFiles.length; i++) {
        console.log("file", attFiles[i])
        console.log("fileName", attFiles[i].name)

        attachedFiles.append('<div>' + attFiles[i].name + '</div>');
    }
});

inputImageFile.on('change', (event) => {
    imageFiles.empty();

    var imgFiles = inputImageFile[0].files;

    for (var i = 0; i < imgFiles.length; i++) {
        imageFiles.append('<div>' + imgFiles[i].name + '</div>');
    }
});