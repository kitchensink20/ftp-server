package service;

import model.File;
import model.User;
import repository.implementations.FileRepositoryImpl;
import repository.interfaces.FileRepository;

import java.util.Collections;
import java.util.List;

public class FileService {
    private FileRepository fileRepository;
    public FileService() {
        this.fileRepository = new FileRepositoryImpl();
    }

    public File getById(int fielId) {
        return fileRepository.getById(fielId);
    }
    public List<File> getByUserId(int userId) {
        return fileRepository.getByUserId(userId);
    }
    public File createFile(File file) {
        return fileRepository.createFile(file);
    }
    public File updateFile(File modifiedFile) {
        return fileRepository.updateFile(modifiedFile);
    }
    public void deleteFile(int fileId) {
        fileRepository.deleteFile(fileId);
    }
    public List<File> getAllUserFiles(User user) {
        if(user != null)
            return user.getFiles();
        return Collections.emptyList();
    }
    public void deleteFileByUserAndFileName(User user, String fileName) {
        if(user != null) {
            for(File file : user.getFiles()) {
                if(file.getName().equals(fileName)){
                    fileRepository.deleteFile(file.getFileId());
                    break;
                }
            }
        }
    }
}
