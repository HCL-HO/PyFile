package com.hec.app.framework.content;

import com.hec.app.entity.BizException;
import com.hec.app.entity.HasCollection;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;




public abstract class CBCollectionResolver<T>
{
    public abstract HasCollection<T> query()
            throws IOException, ServiceException, BizException;
}