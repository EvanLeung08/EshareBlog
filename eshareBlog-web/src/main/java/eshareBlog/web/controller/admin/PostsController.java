/*
+--------------------------------------------------------------------------
|   eshareBlog [#RELEASE_VERSION#]
|   ========================================
|   Copyright (c) 2014, 2015 eshare. All Rights Reserved
|   http://www.eshare.com
|
+---------------------------------------------------------------------------
*/
package eshareBlog.web.controller.admin;

import eshareBlog.base.data.Data;
import eshareBlog.base.lang.Consts;
import eshareBlog.core.data.Post;
import eshareBlog.core.persist.service.GroupService;
import eshareBlog.core.persist.service.PostService;
import eshareBlog.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author evan
 *
 */
@Controller("mng_post_ctl")
@RequestMapping("/admin/posts")
public class PostsController extends BaseController {
	@Autowired
	private PostService postService;
	@Autowired
	private GroupService groupService;
	
	@RequestMapping("/list")
	public String list(String title, ModelMap model, HttpServletRequest request) {
		long id = ServletRequestUtils.getLongParameter(request, "id", Consts.ZERO);
		int group = ServletRequestUtils.getIntParameter(request, "group", Consts.ZERO);

		Pageable pageable = wrapPageable();
		Page<Post> page = postService.paging4Admin(pageable, id, title, group);
		model.put("page", page);
		model.put("title", title);
		model.put("id", id);
		model.put("group", group);
		return "/admin/posts/list";
	}
	
	/**
	 * 跳转到文章编辑方法
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String toUpdate(Long id, ModelMap model) {
		Post ret = postService.get(id);
		model.put("view", ret);
		model.put("groups", groupService.findAll(Consts.STATUS_NORMAL));
		return "/admin/posts/update";
	}
	
	/**
	 * 更新文章方法
	 * @author LBB
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String subUpdate(Post p, HttpServletRequest request) {
		if (p != null) {
			String content = request.getParameter("content");
			p.setContent(content);
			extractImages(p);
			postService.update(p);
		}
		return "redirect:/admin/posts/list";
	}

	@RequestMapping("/featured")
	public @ResponseBody
	Data featured(Long id, HttpServletRequest request) {
		Data data = Data.failure("操作失败");
		int featured = ServletRequestUtils.getIntParameter(request, "featured", Consts.FEATURED_ACTIVE);
		if (id != null) {
			try {
				postService.updateFeatured(id, featured);
				data = Data.success("操作成功", Data.NOOP);
			} catch (Exception e) {
				data = Data.failure(e.getMessage());
			}
		}
		return data;
	}
	
	@RequestMapping("/delete")
	public @ResponseBody Data delete(@RequestParam("id") List<Long> id) {
		Data data = Data.failure("操作失败");
		if (id != null) {
			try {
				postService.delete(id);
				data = Data.success("操作成功", Data.NOOP);
			} catch (Exception e) {
				data = Data.failure(e.getMessage());
			}
		}
		return data;
	}
}
