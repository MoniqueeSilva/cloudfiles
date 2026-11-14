const fileInput = document.getElementById("fileInput");
const dropArea = document.getElementById("dropArea");
const selectedFile = document.getElementById("selectedFile");
const fileName = document.getElementById("fileName");
const fileInfo = document.getElementById("fileInfo");
const uploadButton = document.getElementById("uploadButton");
const removeButton = document.getElementById("removeFile");
const uploadStatus = document.getElementById("uploadStatus");

let currentFile = null;

function formatFileSize(bytes) {
    if (bytes < 1024) return `${bytes} bytes`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

function displayFile(file) {
    if (!file) return;

    currentFile = file;

    fileName.textContent = file.name;
    fileInfo.textContent = `${file.type || "Tipo não identificado"} · ${formatFileSize(file.size)}`;

    selectedFile.hidden = false;
    uploadButton.disabled = false;
    uploadButton.textContent = "Enviar arquivo";
    uploadStatus.textContent = "";
}

function clearSelection() {
    currentFile = null;
    fileInput.value = "";
    selectedFile.hidden = true;
    uploadButton.disabled = true;
    uploadButton.textContent = "Selecionar um arquivo";
    uploadStatus.textContent = "";
}

fileInput.addEventListener("change", () => {
    displayFile(fileInput.files[0]);
});

removeButton.addEventListener("click", clearSelection);

dropArea.addEventListener("dragover", (event) => {
    event.preventDefault();
    dropArea.classList.add("drag-over");
});

dropArea.addEventListener("dragleave", () => {
    dropArea.classList.remove("drag-over");
});

dropArea.addEventListener("drop", (event) => {
    event.preventDefault();
    dropArea.classList.remove("drag-over");

    const file = event.dataTransfer.files[0];

    if (file) {
        displayFile(file);
    }
});

uploadButton.addEventListener("click", () => {
    if (!currentFile) return;

    uploadStatus.textContent =
        "Arquivo selecionado! O envio será habilitado após a integração com o backend.";
});
