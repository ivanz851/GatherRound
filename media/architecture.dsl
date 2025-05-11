workspace {

    model {
        user = person "User" {
            description "Пользователь, взаимодействующий с мобильным приложением"
        }

        mobileApp = softwareSystem "Мобильное приложение" {
            description "Приложение для построения маршрутов в метро"

            user -> this "Использует"

            container_ui = container "UI (Jetpack Compose)" {
                technology "Jetpack Compose"
                description "Интерфейс, отображающий карту метро и маршруты"
            }

            container_graph = container "Граф (ориентированный)" {
                technology "Kotlin"
                description "Математическая модель графа метро"
            }

            container_dijkstra = container "Алгоритм Дейкстры" {
                technology "Kotlin"
                description "Компонент, реализующий алгоритм Дейкстры для поиска кратчайших путей"
            }

            container_data = container "Данные метро Москвы" {
                technology "Kotlin / API"
                description "Получает данные о метро и строит на их основе граф"
            }

            user -> container_ui "Взаимодействует через UI"
            container_ui -> container_graph "Передаёт выбор пользователя"
            container_graph -> container_dijkstra "Использует для расчёта маршрута"
            container_data -> container_graph "Создаёт граф метро"
            container_graph -> container_ui "Возвращает маршруты"
        }
    }

    views {
        systemContext mobileApp {
            include *
            autolayout lr
        }

        container mobileApp {
            include *
            autolayout lr
        }

        theme default
    }
}
