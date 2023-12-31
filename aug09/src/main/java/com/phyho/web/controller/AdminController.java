package com.phyho.web.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.phyho.web.service.AdminService;
import com.phyho.web.util.Util;

@Controller
@RequestMapping("/admin")
public class AdminController {
	// AdminService / AdminDAO / adminMapper
	// count, name, grade
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private Util util;
	
	@GetMapping("/")
	public String adminIndex2() {
		return "foward:/admin/admin";
	}
	
	@GetMapping("/admin")
	public String adminIndex() {
		return "admin/index";
	}
	
	@PostMapping("/login")
	public String adminLogin(@RequestParam Map<String, Object> map, HttpSession session) {
		// System.out.println(map);
		Map<String, Object> result = adminService.adminLogin(map);
		System.out.println(result);
		// {m_grade=9, m_name=asdf, count=1}
		System.out.println(String.valueOf(result.get("count")).equals("1"));
		System.out.println(Integer.parseInt(String.valueOf(result.get("m_grade"))) > 5);
		
		if( util.obj2Int(result.get("count")) == 1	&&  util.obj2Int(result.get("m_grade")) > 5) {
			//System.out.println("진행");
			session.setAttribute("mid", map.get("id"));
			session.setAttribute("mname", result.get("m_name"));
			session.setAttribute("mgrade", result.get("m_grade"));
			// 메인으로 이동하기
			return "redirect:/admin/main";
		} else {
			return "redirect:/admin/admin?error=login";
		}

	}
	
	@GetMapping("/main")
	public String main() {
		return "admin/main"; // 폴더 적어줘야 admin/밑에 main.jsp를 열어줍니다.
	}
	
	@GetMapping("/notice")
	public String notice(Model model) {
		// 1 데이터베이스까지 연결하기
		// 2 데이터 불러오기
		// 3 데이터 jsp로 보내기
		List<Map<String, Object>> list = adminService.list();
		model.addAttribute("list", list);
		//System.out.println(list);
		
		return "admin/notice";
	}
	
	
	@PostMapping("/noticeWrite")
	public String noticeWrite(@RequestParam ("upFile") MultipartFile upfile, @RequestParam Map<String, Object> map) {
		// {title=제목쓰고, content=글을쓰고 글쓰기버튼 누르면, upFile=}
		// System.out.println(map);
		
		// 2023-08-22 요구사항확인
		
		if(!upfile.isEmpty()) {
			// 저장할 경로면 뽑기  request 뽑기
			 HttpServletRequest request = 
				         ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
			String path = request.getServletContext().getRealPath("/upload");
			//System.out.println("실제 경로 : " + path);
			
			
			// upfile 정보보기
			//System.out.println(upfile.getOriginalFilename());
			//System.out.println(upfile.getSize());
			//System.out.println(upfile.getContentType());
			// 진짜로 파일 업로드 하기 경로 + 저장할 파일명
			// String타입의 경로를 file형태로 바꿔주겠습니다.
			// File filePath = new File(path);
			// 중복이 발생할 수 있기때문에 ____ 파일명 + 날짜 + ID + .파일확장자
			// 									UUID + 파일명 + .확장자
			// 									UUID + 파일명 + ID + .확장자
			
			UUID uuid = UUID.randomUUID();
//			String realFileName = uuid.toString() + upfile.getOriginalFilename();
			
			// 날짜 뽑기 SimpleDateFormat
//			Date date = new Date();
//			SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmss");
//			String dateTIme = sdf.format(date);
//			realFileName = dateTIme.toString() + upfile.getOriginalFilename();
			
			// 다른 날짜 뽑기 형식
			LocalDateTime ldt = LocalDateTime.now();
			String format = ldt.format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"));
			// 날짜 + UUID + 실제 파일명으로 사용하겠습니다.
			String realFileName = format + uuid.toString() + upfile.getOriginalFilename();
			
			File newFileName = new File(path, realFileName);
			//이제 파일 올립니다.
			try {
//				upfile.transferTo(newFileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println("저장끝.");
			//FileCopyUtils를 사용하기 위해서는 오리지널 파일을 byte[]로 만들어야 합니다.
			try {
				FileCopyUtils.copy(upfile.getBytes(), newFileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			map.put("upFile", upfile.getOriginalFilename());
			map.put("realFile", realFileName);
		
		}
		
		map.put("mno", 1);
		adminService.noticeWrite(map);
		return "redirect:/admin/notice";
	}
	
	@GetMapping("/mail")
	public String mail() {
		return "admin/mail";
	}
	
	
}









