const fileInput = document.getElementById("fileInput");
const uploadButton = document.getElementById("uploadButton");
const uploadStatus = document.getElementById("uploadStatus");
const fileList = document.getElementById("fileList");

uploadButton.addEventListener("click", () => {
    const file = fileInput.files[0];

    if (!file) {
        uploadStatus.textContent = "Selecione um arquivo primeiro.";
        return;
    }

    uploadStatus.textContent = `Arquivo selecionado: ${file.name}`;
});
