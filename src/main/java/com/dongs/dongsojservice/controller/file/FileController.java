package com.dongs.dongsojservice.controller.file;


import cn.hutool.core.io.FileUtil;
import com.dongs.dongsojservice.common.BaseResponse;
import com.dongs.dongsojservice.common.ErrorCode;
import com.dongs.dongsojservice.common.ResultUtils;
import com.dongs.dongsojservice.constant.FileConstant;
import com.dongs.dongsojservice.exception.BusinessException;
import com.dongs.dongsojservice.manager.CosManager;
import com.dongs.dongsojservice.model.dto.fileRequest.UploadRequest;
import com.dongs.dongsojservice.model.enums.FileUploadBizEnum;
import com.dongs.dongsojservice.model.pojo.User;
import com.dongs.dongsojservice.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;

/**
 * 通用文件上传接口
 *
 * @author dongs
 */

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           UploadRequest uploadRequest){
        // 获取前端传入的业务类型
        String biz = uploadRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"上传文件对应业务参数错误！");
        }
        // 校验上传的文件
        validFile(multipartFile,fileUploadBizEnum);
        // 获取当前登录用户
        User loginUser = userService.getLoginUser();
        // 文件目录根据用户、业务进行划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String fileName = uuid + "-" + multipartFile.getOriginalFilename();
        String filePath = String.format("/%s/%s/%s",fileUploadBizEnum.getValue(),loginUser.getId(),fileName);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filePath,null);
            multipartFile.transferTo(file);
            cosManager.putObject(fileName,file);
            return ResultUtils.success(FileConstant.COS_HOST + filePath);
        }catch (Exception e){
            log.error("file upload error, filepath = " + filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null){
                boolean delete = file.delete();
                if (!delete){
                    log.error("file delete error, filepath = {}", filePath);
                }
            }
        }
    }

    /**
     * 校验文件
     * @param multipartFile 上传的文件
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile  multipartFile, FileUploadBizEnum fileUploadBizEnum){
        // 获取文件的大小
        long fileSize = multipartFile.getSize();
        // 获取文件的后缀名
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 设置文件上传的最大限制
        final long ONE_M = 1024 * 1024L * 5;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)){
            if (fileSize > ONE_M){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件上传大小不能超过5M");
            }
            if (!Arrays.asList("jpeg","jpg","png","svg","webp").contains(fileSuffix)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"上传文件格式类型错误");
            }
        }
    }


}
