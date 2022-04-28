package id.walt.webwallet.backend.wallet

import id.walt.nftkit.services.Chain
import id.walt.nftkit.services.NftService
import id.walt.rest.custodian.CustodianController
import id.walt.webwallet.backend.auth.JWTService
import id.walt.webwallet.backend.auth.UserRole
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.UnauthorizedResponse
import io.javalin.plugin.openapi.dsl.document
import io.javalin.plugin.openapi.dsl.documented

object NFTController {
  val routes
    get() = path("wallet/nfts") {
      get(
        "list/{chain}",
        documented(document().operation {
          it.summary("List my NFTs on the given chain").operationId("listNFTs").addTagsItem("NFTs")
        }, NFTController::listNFTs),
        UserRole.AUTHORIZED
      )
    }

  fun listNFTs(ctx: Context) {
    val userInfo = JWTService.getUserInfo(ctx) ?: throw UnauthorizedResponse("User not authorized")
    val ethAddress = userInfo.ethAccount ?: throw BadRequestResponse("User must be authorized by web3 wallet")
    val chain = ctx.pathParam("chain")
    ctx.json(
      NftService.getAccountNFTsByAlchemy(Chain.valueOf(chain), ethAddress)
    )
  }
}
