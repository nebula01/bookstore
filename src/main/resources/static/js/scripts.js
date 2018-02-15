/**
 * 
 */

$(document).ready(function() {
	// class이름 cartItemQty
	$(".cartItemQty").on('change', function() {
		var id = this.id;
		console.log("???");
		// update-item의 id의 css를 바꿈
		$("#update-item-"+id).css('display', 'inline-block');
	});
});