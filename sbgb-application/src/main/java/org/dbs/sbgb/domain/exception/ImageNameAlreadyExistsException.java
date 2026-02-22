package org.dbs.sbgb.domain.exception;

public class ImageNameAlreadyExistsException extends RuntimeException {
    public ImageNameAlreadyExistsException(String name) {
        super("Image with name '" + name + "' already exists. Please use forceUpdate to overwrite.");
    }
}
