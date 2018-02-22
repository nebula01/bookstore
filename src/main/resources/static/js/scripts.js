/**
 * 
 */

// 수량 텍스트 박스 수정 시 css바꿈
$(document).ready(function() {
	// class이름 cartItemQty
	$(".cartItemQty").on('change', function() {
		var id = this.id;
		console.log("???");
		// update-item의 id의 css를 바꿈
		$("#update-item-"+id).css('display', 'inline-block');
	});
	
	// #theSameAsShippingAddress에 click이벤트 걸어줌
	$("#theSameAsShippingAddress").on('click', checkBillingAddress);
});

// 배송지 주소와 같게 체크시 값 가져오기
function checkBillingAddress() {
	if ($("#theSameAsShippingAddress").is(":checked")) {
		$(".billingAddress").prop("disabled", true);
		$("#billingName").val($("#shippingName").val());
		$("#billingAddress").val($("#shippingStreet").val());
		$("#billingCity").val($("#shippingCity").val());
		$("#billingState").val($("#shippingState").val());
		$("#billingZipcode").val($("#shippingZipcode").val());
	} else {
		$(".billingAddress").prop("disabled", false);
	}
}