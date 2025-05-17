workspace "UI Kotlin + UI JS + Yandex Maps" {

  !identifiers hierarchical

  model {
    user = person "Пользователь"

    app = softwareSystem "GatherRound" {
      uiCompose = container "UI (JetpackCompose)" {
        technology "Kotlin"
        tags "AppBox"
      }
      
      backendKotlin = container "Backend (Kotlin)" {
        technology "Kotlin"
        tags "AppBox"
      }

      uiJs = container "UI (JavaScript)" {
        technology "JavaScript"
        tags "AppBox"
      }
    }
    
    kudaGo = softwareSystem "KudaGo.ru" {
      tags "External"
    }

    yandexMaps = softwareSystem "Yandex Maps" {
      tags "External"
    }
    
    
    user -> app.uiCompose "Взаимодействует"
    app.uiCompose -> user "Взаимодействует"
    
    app.backendKotlin -> kudaGo "Отправляет http запрос"
    kudaGo -> app.backendKotlin "Возвращает ответ в формате JSON"
    app.backendKotlin -> yandexMaps "Отправляет http запрос"

    app.uiCompose -> app.uiJs "Подключает JS-bridge, отправляет команду обновить карту"
    app.uiJs -> app.uiCompose "Передаёт данные о нажатиях"
    
    app.uiCompose -> app.backendKotlin "Передает состояние для обработки"
    app.backendKotlin -> app.uiCompose "Передает новое состояние"


  }

  views {
    container app {
      include *
      include yandexMaps
      include user
      autolayout lr 800 600
    }

    styles {
      element "External" {
        background #444444
        border dashed
        fontSize 50
      }
      
      element "AppBox" {
        fontSize 50
        width 600
      }
      
      element "Person" {
        fontSize 50 
        width 600
      }
      
      relationship "Relationship" {
        width 1000                    
        fontSize 50            
      }
    }

    theme default
  }
}
