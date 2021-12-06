## 7. 장바구니
### add() 장바구니에 담아주는(추가) 기능의  메서드

*  ##### 회원일 경우에만 사용가능한 메서드이며, 회원이 아닐경우 에러를 발생시킴
```
 public boolean add(HttpServletRequest request) throws Exception {
		Member member = (Member)request.getAttribute("member");
		String mode = request.getParameter("mode");
		int isBuy = (mode != null && mode.equals("order"))?1:0;
		String goodsNo = request.getParameter("goodsNo");
		int goodsCnt = 1;
		if (request.getParameter("goodsCnt") != null) {
			goodsCnt = Integer.valueOf(request.getParameter("goodsCnt"));
			if (goodsCnt <= 0) goodsCnt = 1;
		}
		
		if (!MemberDao.isLogin()) {
			throw new Exception("로그인이 필요합니다.");
		}
		
		if (goodsNo == null) {
			throw new Exception("잘못된 접근입니다.");
		}
```
 * #### DB에 데이터를 바인딩해주는 기능을 담당하는 소스로, 이미 상품이 존재한다면 수량 증가, 없다면 추가
```    
		String sql = "SELECT * FROM cart WHERE memNo = ? AND goodsNo = ? AND isBuy = ?";
		ArrayList<DBField> bindings = new ArrayList<>();
		bindings.add(setBinding("Integer", member.getMemNo()));
		bindings.add(setBinding("Integer", goodsNo));
		bindings.add(setBinding("Integer", isBuy));
		Cart cart = DB.executeQueryOne(sql, bindings, new Cart());
		if (cart == null) {
			sql = "INSERT INTO cart (memNo, goodsNo, goodsCnt, isBuy) VALUES (?,?,?,?)";
			bindings = new ArrayList<DBField>();
			bindings.add(setBinding("Integer", member.getMemNo()));
			bindings.add(setBinding("Integer", goodsNo));
			bindings.add(setBinding("Integer", goodsCnt));
			bindings.add(setBinding("Integer", isBuy));
		} else {
			goodsCnt += cart.getGoodsCnt();
			
			sql = "UPDATE cart SET goodsCnt = ? WHERE idx = ?";
			bindings = new ArrayList<DBField>();
			bindings.add(setBinding("Integer", goodsCnt));
			bindings.add(setBinding("Integer", cart.getIdx()));
		}
		
		int rs = executeUpdate(sql, bindings);
		
		return (rs > 0)?true:false;
	}
```  

### delete() 장바구니에 담았던 상품을 선택해서 삭제하는 기능의 메서드
*  #### 장바구니에 추가될 때 바인딩된 idx 번호를 사용해서 구분 

```
	public void delete(HttpServletRequest request) throws Exception {
		String[] idxes = request.getParameterValues("idx");
		if (idxes == null || idxes.length == 0) {
			throw new Exception("삭제할 상품을 선택하세요.");
		}
		
		for (String idx : idxes) {
			ArrayList<DBField> bindings = new ArrayList<>();
			bindings.add(setBinding("Integer", idx));
			String sql = "DELETE FROM cart WHERE idx = ?";
			executeUpdate(sql, bindings);
		}
	}
```

### 장바구니에 담은 제품의 수량 변경 기능의 메서드
* #### 장바구니에 담지 않았던 상품이거나, 수량이 1이상이 아닐경우 에러발생
```
	public boolean updateGoodsCnt(int idx, int goodsCnt) throws Exception {
		
		if (idx == 0) {
			throw new Exception("잘못된 접근입니다.");
		}
		
		if (goodsCnt <= 0) {
			throw new Exception("수량은 1이상 입력하세요.");
		}
		
		ArrayList<DBField> bindings = new ArrayList<>();
		bindings.add(setBinding("Integer", goodsCnt));
		bindings.add(setBinding("Integer", idx));
		String sql = "UPDATE cart SET goodsCnt = ? WHERE idx = ?";
		int rs = DB.executeUpdate(sql, bindings);
		
		return (rs > 0)?true:false;
	}
```
* #### ???
 ```
  	public boolean updateGoodsCnt(HttpServletRequest request) throws Exception {
		int idx = 0;
		int goodsCnt = 1;
		if (request.getParameter("idx") != null) {
			idx = Integer.valueOf(request.getParameter("idx"));
		}
		
		if (request.getParameter("goodsCnt") != null) {
			goodsCnt = Integer.valueOf(request.getParameter("goodsCnt"));
		}
		
		return updateGoodsCnt(idx, goodsCnt);
	}
```

### gets() 장바구니 목록을 보여주는 기능의 메서드
* #### 장바구니 DB에 바인딩 되어있던 데이터들을 가져와서 보여줌
```
public ArrayList<Cart> gets(String[] idxes, boolean isBuy) {
		ArrayList<Cart> list = null;
		if (MemberDao.isLogin()) {
			ArrayList<DBField> bindings = new ArrayList<>();
			Member member = (Member)Req.get().getAttribute("member");
			int memNo = member.getMemNo();
			bindings.add(setBinding("Integer", memNo));
			StringBuilder sb = new StringBuilder("SELECT * FROM cart WHERE memNo = ?");
			
			if (idxes == null) {
				int _isBuy = isBuy?1:0;
				sb.append(" AND isBuy = ?");
				bindings.add(setBinding("Integer", _isBuy));
			} else {
				sb.append(" AND idx IN (");
				boolean isFirst = true;
				for(String idx : idxes) {
					if (!isFirst) sb.append(",");
					sb.append("?");
					bindings.add(setBinding("Integer", idx));
					isFirst = false;
				}
				sb.append(")");
			}
			sb.append(" ORDER BY idx");
			String sql = sb.toString();
			
			list = executeQuery(sql, bindings, new Cart());
		}
		
		return list;
	}
	
	public ArrayList<Cart> gets(HttpServletRequest request) {
		boolean isBuy = (request.getParameterValues("idx") == null)?true:false;
		return gets(request.getParameterValues("idx"), isBuy);
	}
	
	public ArrayList<Cart> gets() {
		return gets(null, false);
	}
	
```
### getSettlePrice() 장바구니에 담긴 상품 총 결제 금액을 보여주는 기능의 메서드
* #### 상품들의 price(가격을) 모두 더해서 보여줌

```
	public int getSettlePrice(ArrayList<Cart> items) {
		int total = 0;
		if (items != null && items.size() > 0) {
			for(Cart item : items) {
				Goods goods = item.getGoodsInfo();
				if (goods == null) continue;
				
				total += goods.getGoodsPrice() * item.getGoodsCnt();
			}
		}
		
		return total;
	}
```

## 각 페이지 별 소개
### 사이트의 메인 페이지
* 검색
* 로그인 -> 로그인 한 경우, 로그아웃으로 변경
* 카테고리
* 마이페이지
* 장바구니

### 카테고리 페이지

### 마이페이지
* 주문내역 확인가능
### 장바구니
* 상품을 선택해서 삭제, 주문이 가능
* 주문 버튼을 누르면 주문하기 페이지로 이동
### 로그인
* 편의성을 위해 네이버로 로그인이 가능
* 로그인 후, 일정시간이 경과하면, 자동으로 로그아웃 됨

### 회원가입
* 비밀번호는 영문자, 숫자, 특수문자를 사용하지 않으면 에러발생
* 핸드폰 번호의 형식과 맞지 않으면 에러발생

### 주문하기
* 이메일의 형식에 맞지않으면 에러발생
* 주문하기 버튼을 누르면 주문 완료
* 주문하기 -> 쇼핑 계속하기로 변경
