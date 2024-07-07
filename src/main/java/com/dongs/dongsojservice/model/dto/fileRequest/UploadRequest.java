package com.dongs.dongsojservice.model.dto.fileRequest;


import lombok.Data;

import java.io.Serializable;

@Data
public class UploadRequest implements Serializable {


    /**
     * 业务类型
     */
    private String biz;

    private static final long serialVersionUID = 1L;
}
