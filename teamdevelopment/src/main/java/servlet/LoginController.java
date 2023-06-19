package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import object.LoginAccount;
import sql.MemberDAO;

// URLパターン
@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/****************************************
	  
	 			GETリクエストメソッド
	 
	 *********************************************/
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// フォワード先
		String forwardPath = null;

		// サーブレットクラスの動作を決定するactionの値を
		// リクエストパラメータから取得
		String action = request.getParameter("action");

		// フォワード先の設定
		if (action == null) {
			//ログイン画面
			forwardPath = "WEB-INF/jsp/login.jsp";
		} else if (action.equals("new")) {
			// 新規会員登録画面
			forwardPath = "WEB-INF/jsp/registerForm.jsp";

		} else if (action.equals("done")) {
			// 登録完了画面
			forwardPath = "WEB-INF/jsp/registerDone.jsp";
		}
		// 設定されたフォワード先にフォワード
		RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPath);
		dispatcher.forward(request, response);

	}

	/****************************************
	  
				POSTリクエストメソッド
	
	*********************************************/
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// リクエストパラメータの取得
		request.setCharacterEncoding("UTF-8");
		String id = request.getParameter("id");
		String address = request.getParameter("address");
		String pass = request.getParameter("pass");
		String newRegister = request.getParameter("newRegister");

		// ログインをしてTOP画面に遷移
		if (newRegister.equals("no")) {
			// ログイン処理を行なう
			// DBに接続して会員情報と照会
			// 一致しなかったらログイン画面に戻る
			MemberDAO memDAO = new MemberDAO();
			// リストに検索結果を格納する
			boolean user_list = memDAO.login(id, pass);
			if (user_list == false) {
				System.out.println("idまたはパスワードが間違っています");
				RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/login.jsp");
				dispatcher.forward(request, response);
			}

			//セッションスコープに登録ユーザーを保存
			LoginAccount loginAccount = new LoginAccount(id,pass);
			HttpSession session = request.getSession();
			session.setAttribute("loginAccount", loginAccount);

			// フォワード(TOP画面)
			RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/top.jsp");
			dispatcher.forward(request, response);

			// 新規会員登録をして会員情報をDBに登録	
		} else if (newRegister.equals("ok")) {
			// Validationメソッドを実行して
			// 条件と一致していたら

			// 登録するユーザーの情報を設定
			MemberDAO memDAO = new MemberDAO();
			// DB接続して会員情報を登録(INSERT)
			memDAO.insert(id, address, pass);
			// フォワード(ログイン画面) リダイレクトに変更する!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			//RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/login.jsp");
			//dispatcher.forward(request, response);
			response.sendRedirect("WEB-INF/jsp/login.jsp");
		}
	}
}