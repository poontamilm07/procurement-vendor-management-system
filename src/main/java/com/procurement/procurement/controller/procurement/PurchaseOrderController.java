package com.procurement.procurement.controller.procurement;

import com.procurement.procurement.entity.procurement.PurchaseOrder;
import com.procurement.procurement.service.procurement.PurchaseOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/procurement/purchase")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    // ===================== CREATE =====================
    @PostMapping("/create")
    public ResponseEntity<PurchaseOrder> createPurchase(
            @RequestBody PurchaseOrder purchaseOrder
    ) {
        PurchaseOrder saved = purchaseOrderService.createPurchaseOrder(purchaseOrder);
        return ResponseEntity.ok(saved);
    }

    // ===================== GET ALL =====================
    @GetMapping("/all")
    public ResponseEntity<List<PurchaseOrder>> getAll() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    // ===================== GET BY ID =====================
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                purchaseOrderService.getPurchaseOrderById(id)
        );
    }

    // ===================== UPDATE =====================
    @PutMapping("/update/{id}")
    public ResponseEntity<PurchaseOrder> update(
            @PathVariable Long id,
            @RequestBody PurchaseOrder purchaseOrder
    ) {
        return ResponseEntity.ok(
                purchaseOrderService.updatePurchaseOrder(id, purchaseOrder)
        );
    }

    // ===================== DELETE =====================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.ok("Purchase Order deleted successfully");
    }
}
