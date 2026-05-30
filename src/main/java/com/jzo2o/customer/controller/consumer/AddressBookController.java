package com.jzo2o.customer.controller.consumer;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.customer.model.domain.AddressBook;
import com.jzo2o.customer.model.dto.request.AddressBookPageQueryReqDTO;
import com.jzo2o.customer.model.dto.request.AddressBookUpsertReqDTO;
import com.jzo2o.customer.service.IAddressBookService;
import com.jzo2o.mvc.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("consumer/address-book")
@Api(tags = "地址簿相关接口")
public class AddressBookController {


    @Resource
    private IAddressBookService addressBookService;

    @PostMapping
    @ApiOperation("地址簿新增")
    public void add(@RequestBody AddressBookUpsertReqDTO addressBookUpsertReqDTO) {
        log.info("地址簿新增，参数:{}", addressBookUpsertReqDTO);
        Long userId = UserContext.currentUserId();

        AddressBook addressBook = new AddressBook();
        BeanUtil.copyProperties(addressBookUpsertReqDTO, addressBook);
        addressBook.setUserId(userId);

        addressBookService.save(addressBook);
    }


    @GetMapping("/page")
    @ApiOperation("地址簿分页查询")
    public PageResult<AddressBook> page(AddressBookPageQueryReqDTO reqDTO) {
        log.info("地址簿分页查询，参数:{}", reqDTO);

        // 1. 分页
        Page<AddressBook> page = new Page<>(reqDTO.getPageNo(), reqDTO.getPageSize());

        // 2. 查询条件
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, UserContext.currentUserId())
                        .eq(AddressBook::getIsDeleted, 0);

        // 3. 默认排序（纯Lambda，自动匹配数据库，永不报错）
        wrapper.orderByDesc(AddressBook::getIsDefault)
                .orderByDesc(AddressBook::getUpdateTime);

        // 4. 分页查询
        IPage<AddressBook> iPage = addressBookService.page(page, wrapper);

        // 5. 封装结果
        PageResult<AddressBook> result = new PageResult<>();
        result.setList(iPage.getRecords());
        result.setTotal(iPage.getTotal());
        result.setPages(iPage.getPages());
        return result;
    }


@GetMapping("/{id}")
    @ApiOperation("地址簿查询")
    public AddressBook get(@PathVariable("id") Long id)

    {
        return addressBookService.getById(id);
    }


    @PutMapping("/{id}")
    @ApiOperation("地址簿修改")
    @Transactional
    public void  update(@PathVariable("id") Long id,
                        @RequestBody AddressBookUpsertReqDTO addressBookUpsertReqDTO)
    {
        AddressBook addressBook = addressBookService.getById(id);
        if (ObjectUtils.isNotNull(addressBook)) {
            BeanUtil.copyProperties(addressBookUpsertReqDTO, addressBook);
            addressBook.setId(id); // 确保 ID 一致
            // 如果修改后的地址簿被设置为默认地址簿，则需要将其他地址簿的默认状态取消
            if(Objects.equals(addressBookUpsertReqDTO.getIsDefault(), new Integer(1)))
            {
                LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper();
                wrapper.eq(AddressBook::getUserId, UserContext.currentUserId())
                        .eq(AddressBook::getIsDefault, 1);
                AddressBook defaultAddressBook = addressBookService.getOne(wrapper);
                if(ObjectUtils.isNotNull(defaultAddressBook))
                {
                    defaultAddressBook.setIsDefault(0);
                    addressBookService.updateById(defaultAddressBook);
                }
            }
            addressBook.setIsDefault(addressBookUpsertReqDTO.getIsDefault())
                            .setUpdateBy(UserContext.currentUserId())
                                    .setUpdateTime(LocalDateTime.now());
            addressBookService.updateById(addressBook);
        }
    }


    @DeleteMapping("/batch")
    @ApiOperation("地址簿批量删除")
    public void batchdelete(@RequestBody List<Long> ids)
    {
        addressBookService.removeByIds(ids);
    }

    @GetMapping("/defaultAddress")
    @ApiOperation("获取默认地址簿")
    public AddressBook getDefaultAddressBook(  )
    {
        return addressBookService.getDefaultAddressBook();
    }


    @PutMapping("/default")
    @Transactional
    public void setDefault(@RequestParam Long id)
    {
        log.info("设置默认地址簿，id:{}", id);
        AddressBook addressBook = addressBookService.getById(id);


            LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper();
            wrapper.eq(AddressBook::getUserId, UserContext.currentUserId())
                    .eq(AddressBook::getIsDefault, 1);
            AddressBook defaultAddressBook = addressBookService.getOne(wrapper);
            if(ObjectUtils.isNotNull(defaultAddressBook))
            {
                defaultAddressBook.setIsDefault(0);
                addressBookService.updateById(defaultAddressBook);
            }

        addressBook.setIsDefault(1)
                .setUpdateBy(UserContext.currentUserId())
                .setUpdateTime(LocalDateTime.now());
            addressBookService.updateById(addressBook);

    }
}
